package Utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class SparrowObject {
  private String id;
  private int byteSize;
  private TypeStruct type;
  private LinkedHashMap<String, Integer> fieldByteOffsets;

  public SparrowObject(String id, int byteSize, TypeStruct type) {
    this.id = id;
    this.type = type;
    this.byteSize = byteSize;
    fieldByteOffsets = new LinkedHashMap<>();
  }

  public SparrowObject(SparrowObject other) {
    this.id = other.id;
    this.type = other.type;
    this.byteSize = other.byteSize;
    this.fieldByteOffsets = new LinkedHashMap<>(other.fieldByteOffsets);
  }

  public String Id() {
    return id;
  }

  public void SetId(String id) {
    this.id = id;
  }

  public int ByteSize() {
    return byteSize;
  }

  public TypeStruct TypeStruct() {
    return type;
  }

  public LinkedHashMap<String, Integer> FieldByteOffsets() {
    return fieldByteOffsets;
  }

  public String Field(int byteOffset) {
    for (Map.Entry<String, Integer> field : fieldByteOffsets.entrySet()) {
      if (byteOffset == field.getValue()) {
        return field.getKey();
      }
    }

    return null;
  }

  public Integer FieldByteOffset(String field) {
    return fieldByteOffsets.get(field);
  }

  public void PutFieldByteOffset(String field, int byteOffset) {
    fieldByteOffsets.put(field, byteOffset);
  }

  public void ReplaceFieldByteOffset(String field, String newField) {
    int offset = fieldByteOffsets.get(field);
    fieldByteOffsets.remove(field);
    fieldByteOffsets.put(newField, offset);
  }

  public String ToString() {
    String obj = "id: " + id + ", byteSize: " + byteSize + ", type: " + type.Type() + "\n";
    obj += "fieldByteOffsets: " + fieldByteOffsets.toString() + "\n";
    return obj;
  }
}
