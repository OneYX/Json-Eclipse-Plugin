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
package com.boothen.jsonedit.core.model.jsonnode;

import com.boothen.jsonedit.core.model.node.Node;

public class JsonNode {

	private Node key;
	private Node value;
	private JsonType jsonType;


	public JsonNode(Node key, Node value, JsonType jsonType) {
		super();
		this.key = key;
		this.value = value;
		this.jsonType = jsonType;
	}

	public Node getKey() {
		return key;
	}
	public void setKey(Node key) {
		this.key = key;
	}
	public Node getValue() {
		return value;
	}
	public void setValue(Node value) {
		this.value = value;
	}
	public JsonType getJsonType() {
		return jsonType;
	}
	public void setJsonType(JsonType jsonType) {
		this.jsonType = jsonType;
	}
	
	public int getStart() {
		Node startNode = this.getKey();
		if (startNode == null) {
			startNode = this.getValue();
		}

		return startNode.getStart();
	}

	public int getEnd() {

		Node endNode = this.getValue();
		if (endNode == null) {
			endNode = this.getKey();
		}

		if (endNode == null) {
			return 0;
		}
		return endNode.getEnd();
	}
	

	@Override
	public String toString() {
		String toString = jsonType.toString();
		String keyString = (key != null) ? key.getValue() + "," : "";
//		toString += ", " + position.offset + ", " + position.length;
		toString += ", " + keyString + ", " + ((value != null) ? value.getValue() : "");
		return toString;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((jsonType == null) ? 0 : jsonType.hashCode());
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JsonNode other = (JsonNode) obj;
		if (jsonType == null) {
			if (other.jsonType != null)
				return false;
		} else if (!jsonType.equals(other.jsonType))
			return false;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

}
