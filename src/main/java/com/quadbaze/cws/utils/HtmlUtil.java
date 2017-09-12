package com.quadbaze.cws.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * Created by Olatunji on 6/19/2016.
 */

public class HtmlUtil {

	private HtmlUtil() {}

	public enum SourceType{
		HTML, FILE, URL
	}



    /**
     * <p>Extracts html text without the tags from a File, or from the response of a URL call, or from an html String </p>
     * <p>Gets the combined text of this element and all its children. Whitespace is normalized and trimmed.
     *    For example, given HTML <p>Hello <b>there</b> now! </p>, p.text() returns "Hello there now!"
     * </p>
     * @param sourceType Options are defined by {@link SourceType}
     * @param args This is determined by {@link SourceType}
     *             <ul>
     *              <li>if {@code SourceType} is {@code SourceType.HTML} then args will be an html string</li>
     *              <li>if {@code SourceType} is {@code SourceType.FILE} then args will be the file path</li>
     *              <li>if {@code SourceType} is {@code SourceType.URL} then args will be a http or https URL to fetch contents from</li>
     *             </ul>
     * @param isJarResource used to specify whether the context where this call is made would be from within a jar file/deployment or not
     *
     * @return the resulting html String
     */
	public static String extractHtmlText(SourceType sourceType, String args, boolean isJarResource) {
		String text = "";
		try{
			switch(sourceType){
				case HTML:
					text = Jsoup.parse(args).text();
					break;
				case FILE:
					text = Jsoup.parse(ResourceLoader.load(args, isJarResource), StandardCharsets.UTF_8.name()).text();
					break;
				case URL:
					text = Jsoup.connect(args).get().text();
					break;
				default: break;
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return text;
	}


    /**
     * <p>Extracts html string of the specified do element </p>
     * @param sourceType Options are defined by {@link SourceType}
     * @param args This is determined by {@link SourceType}
     *             <ul>
     *              <li>if {@code SourceType} is {@code SourceType.HTML} then args will be an html string</li>
     *              <li>if {@code SourceType} is {@code SourceType.FILE} then args will be the file path</li>
     *              <li>if {@code SourceType} is {@code SourceType.URL} then args will be a http or https URL to fetch contents from</li>
     *             </ul>
     * @param domElement Finds the specified dom element
     *              <ul>
     *                  <li>el.select("a[href]") - finds links (a tags with href attributes) </li>
     *                  <li>el.select("a[href*=example.com]")- finds links pointing to example.com (loosely)</li>
     *              </ul>
     * @param noOfElelments specifies whether to return the html String as the full dom document
     *                       or to return contents of only the body of the html document
     *
     * @return the resulting html String
     */
	public static String extractHtmlTags(SourceType sourceType, String args, String domElement, int noOfElelments) {
		String html = "";
		Document doc = null;
		try {
			switch (sourceType) {
				case HTML:
					doc = Jsoup.parse(args);
					break;
				case FILE:
					doc = Jsoup.parse(new File(args), StandardCharsets.UTF_8.name());
					break;
				case URL:
					doc = Jsoup.connect(args).get();
					break;
				default:
					break;
			}

			if(doc != null){
				Elements elements = doc.select(domElement);
				noOfElelments = noOfElelments < elements.size() ? noOfElelments : elements.size();
				for (int n = 0; n < noOfElelments; n++) {
					html = html + elements.get(n).outerHtml();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return html;
	}


    /**
     * <p>Extracts html string from a File, or from the response of a URL call, or from an html String </p>
     * @param sourceType Options are defined by {@link SourceType}
     * @param asFullDocument specifies whether to return the html String as the full dom document
     *                       or to return contents of only the body of the html document
     * @param args This is determined by {@link SourceType}
     *             <ul>
 *                      <li>if {@code SourceType} is {@code SourceType.HTML} then args will be an html string</li>
     *                  <li>if {@code SourceType} is {@code SourceType.FILE} then args will be the file path</li>
     *                  <li>if {@code SourceType} is {@code SourceType.URL} then args will be a http or https URL to fetch contents from</li>
     *             </ul>
     * @param isJarResource used to specify whether the context where this call is made would be from within a jar file/deployment or not
     *
     * @return the resulting html String
     */
	public static String extractHtml(SourceType sourceType, boolean asFullDocument, String args, boolean isJarResource) {
		String html = "";
		try{
			switch(sourceType){
				case HTML:
					html = asFullDocument ? Jsoup.parse(args).html() : Jsoup.parseBodyFragment(args).body().html();
					break;
				case FILE:
					html = asFullDocument ? Jsoup.parse(ResourceLoader.load(args, isJarResource), StandardCharsets.UTF_8.name()).html() : Jsoup.parseBodyFragment(Jsoup.parse(ResourceLoader.load(args, isJarResource), StandardCharsets.UTF_8.name()).html()).body().html();
					break;
				case URL:
					html = asFullDocument ? Jsoup.connect(args).get().html() : Jsoup.parseBodyFragment(Jsoup.connect(args).get().html()).body().html();
					break;
				default: break;
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return html;
	}

	public static String toHtml(String templatePath, Attributes attr){
		String htmlContent = HtmlUtil.extractHtml(HtmlUtil.SourceType.FILE, false, templatePath, false);
		for(String key : attr.keySet()){
			attr.assign(key, attr.get(key));
		}
		return attr.applyTo(htmlContent);
	}
}