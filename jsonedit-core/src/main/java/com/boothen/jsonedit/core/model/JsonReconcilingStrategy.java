/*******************************************************************************
 * Copyright 2014 Boothen Technology Ltd.
 *
 * Licensed under the Eclipse Public License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *   
 * https://eclipse.org/org/documents/epl-v10.html
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.boothen.jsonedit.core.model;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.TypedPosition;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.swt.widgets.Display;

import com.boothen.jsonedit.core.editors.JsonTextEditor;
import com.boothen.jsonedit.core.model.jsonnode.JsonNode;
import com.boothen.jsonedit.core.model.jsonnode.JsonNodeBuilder;
import com.boothen.jsonedit.folding.JsonFoldingPositionsBuilder;
import com.boothen.jsonedit.model.entry.JsonEntry;
import com.boothen.jsonedit.model.entry.JsonEntryBuilder;
import com.boothen.jsonedit.type.JsonDocumentType;

public class JsonReconcilingStrategy implements IReconcilingStrategy, IReconcilingStrategyExtension {

    private JsonTextEditor textEditor;

    private IDocument fDocument;

    @Override
    public void reconcile(IRegion partition) {
        initialReconcile();
    }

    @Override
    public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
        initialReconcile();
    }

    @Override
    public void setDocument(IDocument document) {
        this.fDocument = document;

    }

    @Override
    public void initialReconcile() {
        try {
            fDocument.removePositionCategory(JsonEntryBuilder.JSON_ELEMENTS);
            fDocument.addPositionCategory(JsonEntryBuilder.JSON_ELEMENTS);
        } catch (BadPositionCategoryException e) {
            e.printStackTrace();
        }
        
        
        scan(0, fDocument.getLength());
        parse();
    }

    @Override
    public void setProgressMonitor(IProgressMonitor monitor) {

    }
    
    private void scan(int offset, int length) {
        JsonPartitionScanner jsonPartitionScanner = new JsonPartitionScanner();
        jsonPartitionScanner.setRange(fDocument, offset, length);
        IToken nextToken = jsonPartitionScanner.nextToken();
        while (!nextToken.isEOF()) {
            if (nextToken.getData() != null && JsonDocumentType.DOCUMENT_TYPES.contains(nextToken.getData())) {
                try {
                    fDocument.addPosition(JsonEntryBuilder.JSON_ELEMENTS, new TypedPosition(jsonPartitionScanner.getTokenOffset(), 
                            jsonPartitionScanner.getTokenLength(), (String) nextToken.getData()));
                } catch (BadLocationException e) {
                    e.printStackTrace();
                } catch (BadPositionCategoryException e) {
                    e.printStackTrace();
                } 
            }
            nextToken = jsonPartitionScanner.nextToken();
        }
        
    }

    private void parse() {

        final List<JsonEntry> jsonEntries = new JsonEntryBuilder().buildJsonEntries(fDocument);
        final List<JsonNode> jsonNodes = new JsonNodeBuilder().buildJsonNodes(jsonEntries);
        final List<Position> fPositions = new JsonFoldingPositionsBuilder(jsonNodes).buildFoldingPositions();

        if (textEditor != null) {
            Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    textEditor.updateFoldingStructure(fPositions);
                    textEditor.updateContentOutlinePage(jsonNodes);
                }

            });
        }
    }

    public void setTextEditor(JsonTextEditor textEditor) {
        this.textEditor = textEditor;
    }
}
