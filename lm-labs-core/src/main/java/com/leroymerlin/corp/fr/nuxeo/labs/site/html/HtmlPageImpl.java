package com.leroymerlin.corp.fr.nuxeo.labs.site.html;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;

public class HtmlPageImpl implements HtmlPage {

	public static final String DOCTYPE = "HtmlPage";

	private final DocumentModel doc;

	private List<HtmlSection> sections;

	public HtmlPageImpl(DocumentModel doc) {
		this.doc = doc;
	}

	@Override
	public void setTitle(String title) throws ClientException {
		doc.setPropertyValue("dc:title", title);

	}

	@Override
	public void setDescription(String description) throws ClientException {
		doc.setPropertyValue("dc:description", description);

	}

	@Override
	public String getTitle() throws ClientException {
		return (String) doc.getPropertyValue("dc:title");
	}

	@Override
	public String getDescription() throws ClientException {
		return (String) doc.getPropertyValue("dc:description");
	}

	@Override
	public DocumentModel getDocument() {
		return doc;
	}

	@Override
	public List<HtmlSection> getSections() throws ClientException {
		if (sections == null) {
			@SuppressWarnings("unchecked")
			List<Map<String, Serializable>> sectionsMap = (List<Map<String, Serializable>>) doc
					.getPropertyValue("html:sections");
			sections = new ArrayList<HtmlSection>(sectionsMap.size());
			for (Map<String, Serializable> map : sectionsMap) {
				sections.add(new HtmlSection(this, map));
			}

		}
		return sections;

	}

	@Override
	public HtmlSection addSection() throws ClientException {
		List<HtmlSection> sections = getSections();
		HtmlSection returnedSection = new HtmlSection(this);
		sections.add(returnedSection);
		update();

		return returnedSection;
	}

	void update() throws ClientException {
		List<Map<String, Serializable>> sectionsMap = new ArrayList<Map<String, Serializable>>();

		for (HtmlSection section : sections) {
			sectionsMap.add(section.toMap());
		}

		doc.setPropertyValue("html:sections", (Serializable) sectionsMap);

	}

	@Override
	public HtmlSection section(int index) throws ClientException {
		return getSections().get(index);
	}

	public HtmlSection addSectionBefore(HtmlSection htmlSection) throws ClientException {
		List<HtmlSection> sections = getSections();
		HtmlSection section = new HtmlSection(this);
		sections.add(sections.indexOf(htmlSection), section);
		update();
		return section;
		
		
	}

	public void removeSection(HtmlSection htmlSection) throws ClientException {
		getSections().remove(htmlSection);
		update();
	}

}