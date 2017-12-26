package com.danielcorroto.directorius.model.comparator;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;
import java.util.ResourceBundle;

import com.danielcorroto.directorius.controller.type.PhoneTypeEnum;

/**
 * Comparador para ordenar PhoneTypeEnum según el idioma
 * 
 * @author Daniel Corroto Quirós
 *
 */
public class PhoneTypeComparator implements Comparator<PhoneTypeEnum> {
	/**
	 * Para i18n
	 */
	private ResourceBundle rb;

	/**
	 * Constructor por defecto
	 * 
	 * @param rb
	 *            Recurso para multilenguaje
	 */
	public PhoneTypeComparator(ResourceBundle rb) {
		this.rb = rb;
	}

	@Override
	public int compare(PhoneTypeEnum arg0, PhoneTypeEnum arg1) {
		String text0 = rb.getString(arg0.getI18n());
		String text1 = rb.getString(arg1.getI18n());

		if (text0 == null && text1 == null) {
			return 0;
		} else if (text0 == null && text1 != null) {
			return -1;
		} else if (text0 != null && text1 == null) {
			return 1;
		} else {
			Collator collator = Collator.getInstance(Locale.getDefault());
			return collator.compare(text0, text1);
		}
	}

}
