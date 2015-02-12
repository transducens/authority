package com.cervantesvirtual.metadata;

import java.util.logging.Logger;

/**
 * Dublin Core Metadata elements (qualified for subject, creator and
 * contributor).
 * 
 * @author RCC.
 * @verion 2011.03.10
 */
public enum DCTerm {
	ABSTRACT("abstract"),
	ACCESS_RIGHTS("accessRights"),
	ACCRUAL_METHOD("accrualMethod"),
	ACCRUAL_PERIODICITY("accrualPeriodicity"),
	ACCRUAL_POLICY("accrualPolicy"),
	ALTERNATIVE("alternative"),
	AUDIENCE("audience"),
	AVAILABLE("available"),
	BIBLIOGRAPHIC_CITATION("bibliographicCitation"),
	CONFORMS_TO("conformsTo"),
	CONTRIBUTOR("contributor"),
	CONTRIBUTOR_NAME("contributor.name"),
	CONTRIBUTOR_DATE("contributor.date"),
	COVERAGE("coverage"),
	CREATED("created"),
	CREATOR("creator"),
	CREATOR_NAME("creator.name"),
	CREATOR_DATE("creator.date"),
	DATE("date"),
	DATE_ACCEPTED("dateAccepted"),
	DATE_COPYRIGHTED("dateCopyrighted"),
	DATE_SUBMITTED("dateSubmitted"),
	DESCRIPTION("description"),
	EDUCATION_LEVEL("educationLevel"),
	EXTENT("extent"),
	FORMAT("format"),
	HAS_FORMAT("hasFormat"),
	HAS_PART("hasPart"),
	HAS_VERSION("hasVersion"),
	IDENTIFIER("identifier"),
	IDENTIFIER_SLUG("identifier.slug"),
	IDENTIFIER_URL("identifier.url"),
	INSTRUCTIONAL_METHOD("instructionalMethod"),
	IS_FORMAT_OF("isFormatOf"),
	IS_PART_OF("isPartOf"),
	IS_REFERENCED_BY("isReferencedBy"),
	IS_REPLACED_BY("isReplacedBy"),
	IS_REQUIRED_BY("isRequiredBy"),
	ISSUED("issued"),
	IS_VERSION_OF("isVersionOf"),
	LANGUAGE("language"),
	LICENSE("license"),
	MEDIATOR("mediator"),
	MEDIUM("medium"),
	MODIFIED("modified"),
	PROVENANCE("provenance"),
	PUBLISHER("publisher"),
	REFERENCES("references"),
	RELATION("relation"),
	REPLACES("replaces"),
	REQUIRES("requires"),
	RIGHTS("rights"),
	RIGHTS_HOLDER("rightsHolder"),
	SOURCE("source"),
	SPATIAL("spatial"),
	SUBJECT("subject"),
	SUBJECT_LCSH("subject.LCSH"),
	SUBJECT_UDC("subject.UDC"),
	TABLE_OF_CONTENTS("tableOfContents"),
	TEMPORAL("temporal"),
	TITLE("title"),
	TYPE("type"),
	VALID("valid");
		
	private static Logger log = Logger.getLogger(DCTerm.class.getName());
	
	private final String name; // Term name

	/**
	 * Initialise name with the term name
	 * 
	 * @param name
	 *            the DCterm name
	 */
	DCTerm(String name) {
		this.name = name;
	}

	/**
	 * @return the DC term name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the tag as an XML element's tag.
	 */
	public String toXMLTag() {
		return "dcterm:" + name;
	}

	public String toString(){
		return name;
	}
	
	/**
	 * @return the DCTerm for a given XML tag
	 * @throws MetadataException
	 */
	public static DCTerm term(String tag) throws MetadataException {
		for (DCTerm dcterm : values()) {
			
			//log.info("dcterm.name=" + dcterm.name);
			
			if (tag.matches("(dcterm:|dc:)?" + dcterm.name)) {
				return dcterm;
			}
		}
		throw new MetadataException("Not a valid DC term: " + tag);
	}
}
