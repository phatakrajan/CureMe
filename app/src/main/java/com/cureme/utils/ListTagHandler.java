package com.cureme.utils;

import android.text.Editable;
import android.text.Html;
import android.text.style.BulletSpan;
import android.text.style.LeadingMarginSpan;

import org.xml.sax.XMLReader;

import java.util.Vector;

public class ListTagHandler implements Html.TagHandler {

	private int m_index = 0;
	private Vector<String> m_parents = new Vector<String>();

	@Override
	public void handleTag(boolean opening, String tag, Editable output,
			XMLReader xmlReader) {
		if (tag.equals("ul") || tag.equals("ol") || tag.equals("dd")) {
			if (output.toString().endsWith("\n") == false) {
				output.append("\n");
			}
			if (opening) {
				m_parents.add(tag);
			} else {
				m_parents.remove(tag);
			}

			if (opening == false && tag.equals("ul")){
				output.append("\n");
			}
			m_index = 0;
		} else if (tag.equals("li") && !opening) {
			handleListTag(output);
		}
	}

	private void handleListTag(Editable output) {
		if (m_parents.lastElement().equals("ul")) {
			output.append("\n");
			String[] split = output.toString().split("\n");

			int lastIndex = split.length - 1;
			int start = output.length() - split[lastIndex].length() - 1;
			output.setSpan(new BulletSpan(15 * m_parents.size()), start,
					output.length(), 0);
		} else if (m_parents.lastElement().equals("ol")) {
			m_index++;

			output.append("\n");
			String[] split = output.toString().split("\n");

			int lastIndex = split.length - 1;
			int start = output.length() - split[lastIndex].length() - 1;
			output.insert(start, m_index + ". ");
			output.setSpan(
					new LeadingMarginSpan.Standard(15 * m_parents.size()),
					start, output.length(), 0);
		}

	}

}
