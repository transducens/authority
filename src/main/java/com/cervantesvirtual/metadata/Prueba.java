package com.cervantesvirtual.metadata;

import java.io.*;

public class Prueba {
	public static void main(String[] args) {
		Collection collection = new Collection(MetadataFormat.MARC);
		for (String arg : args) {
			File file = new File(arg);
			collection.addMARCXML(file);
		}
		for (Record record : collection.getRecords()) {
			String id = record.getId();
			for (Field field : record.getFields()) {
				if (field.getType() != FieldType.MARC_LEADER) {
					System.out.println(id + " " + field.toString());
				}
			}
		}
	}
}
