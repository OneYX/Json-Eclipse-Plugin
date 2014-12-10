package com.boothen.jsonedit.core.editors;


import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;

import com.boothen.jsonedit.core.model.JsonPartitionScanner;
import com.boothen.jsonedit.core.model.JsonReconcilingStrategy;
import com.boothen.jsonedit.core.text.JsonIndentLineAutoEditStrategy;
import com.boothen.jsonedit.preferences.JsonPreferenceStore;
import com.boothen.jsonedit.text.JsonConstantWordScanner;
import com.boothen.jsonedit.text.JsonNumberScanner;
import com.boothen.jsonedit.text.JsonStringScanner;
import com.boothen.jsonedit.text.LineEndingUtil;

/**
 * JsonSourceViewerConfiguration manages the coloring of the text.
 *
 * @author Matt Garner
 *
 */
public class JsonSourceViewerConfiguration extends TextSourceViewerConfiguration {

	private JsonTextEditor textEditor;
	private JsonIndentLineAutoEditStrategy jsonIndentLineAutoEditStrategy;
	private JsonStringScanner jsonStringScanner;
	private JsonConstantWordScanner jsonTrueScanner;
	private JsonConstantWordScanner jsonFalseScanner;
	private JsonConstantWordScanner jsonNullScanner;
	private JsonNumberScanner jsonNumberScanner;
	private JsonPreferenceStore store;

	public JsonSourceViewerConfiguration(JsonTextEditor textEditor, JsonPreferenceStore store) {
		super();
		this.store = store;
		this.textEditor = textEditor;
		boolean spaces = store.getSpacesForTab();
		int numSpaces = store.getTabWidth();
		String lineEnding = "\n";
		jsonIndentLineAutoEditStrategy = new JsonIndentLineAutoEditStrategy(spaces, numSpaces, lineEnding);
		jsonStringScanner = new JsonStringScanner();
		jsonTrueScanner = new JsonConstantWordScanner("true");
		jsonFalseScanner = new JsonConstantWordScanner("false");
		jsonNullScanner = new JsonConstantWordScanner("null");
		jsonNumberScanner = new JsonNumberScanner();
	}

	@Override
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler= new PresentationReconciler();
		
		DefaultDamagerRepairer dr= new DefaultDamagerRepairer(jsonStringScanner);	
		reconciler.setDamager(dr, JsonPartitionScanner.JSON_STRING);
		reconciler.setRepairer(dr, JsonPartitionScanner.JSON_STRING);
		
		dr= new DefaultDamagerRepairer(jsonTrueScanner);	
		reconciler.setDamager(dr, JsonPartitionScanner.JSON_TRUE);
		reconciler.setRepairer(dr, JsonPartitionScanner.JSON_TRUE);
		
		dr= new DefaultDamagerRepairer(jsonFalseScanner);	
		reconciler.setDamager(dr, JsonPartitionScanner.JSON_FALSE);
		reconciler.setRepairer(dr, JsonPartitionScanner.JSON_FALSE);
		
		dr= new DefaultDamagerRepairer(jsonNullScanner);	
		reconciler.setDamager(dr, JsonPartitionScanner.JSON_NULL);
		reconciler.setRepairer(dr, JsonPartitionScanner.JSON_NULL);
		
		dr= new DefaultDamagerRepairer(jsonNumberScanner);	
		reconciler.setDamager(dr, JsonPartitionScanner.JSON_NUMBER);
		reconciler.setRepairer(dr, JsonPartitionScanner.JSON_NUMBER);
		
		return reconciler;
	}
	
	

	@Override
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] { 
				IDocument.DEFAULT_CONTENT_TYPE,
				JsonPartitionScanner.JSON_OBJECT_CLOSE,
				JsonPartitionScanner.JSON_OBJECT_OPEN,
				JsonPartitionScanner.JSON_ARRAY_CLOSE,
				JsonPartitionScanner.JSON_ARRAY_OPEN,
				JsonPartitionScanner.JSON_STRING,
				JsonPartitionScanner.JSON_NUMBER,
				JsonPartitionScanner.JSON_TRUE,
				JsonPartitionScanner.JSON_FALSE,
				JsonPartitionScanner.JSON_NULL,
				JsonPartitionScanner.JSON_COMMA,
				JsonPartitionScanner.JSON_COLON};
	}

	@Override
	public IReconciler getReconciler(ISourceViewer sourceViewer) {
		JsonReconcilingStrategy strategy = new JsonReconcilingStrategy();
		strategy.setTextEditor(textEditor);
        MonoReconciler reconciler = new MonoReconciler(strategy,false);
        return reconciler;
	}

	@Override
	public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType) {
		return new IAutoEditStrategy[] { jsonIndentLineAutoEditStrategy };
	}

	public void handlePreferenceStoreChanged() {
		boolean spacesForTab = store.getSpacesForTab();
		int tabWidth = store.getTabWidth();

		String lineEnding = getTextEditorLineEnding();
		textEditor.updateTabWidth(tabWidth);
		jsonIndentLineAutoEditStrategy.initPreferences(spacesForTab, tabWidth, lineEnding);
		jsonStringScanner.reinit();
		jsonTrueScanner.reinit();
		jsonFalseScanner.reinit();
		jsonNullScanner.reinit();
		jsonNumberScanner.reinit();
		
	}

	private String getTextEditorLineEnding() {
		IFile file = (IFile) textEditor.getEditorInput().getAdapter(IFile.class);
		return LineEndingUtil.determineProjectLineEnding(file);
	}
}
