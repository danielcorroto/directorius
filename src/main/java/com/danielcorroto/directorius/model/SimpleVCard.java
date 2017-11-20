package com.danielcorroto.directorius.model;

import ezvcard.VCard;
import ezvcard.property.FormattedName;
import ezvcard.property.Uid;

/**
 * Datos del contacto necesarios para mostrar en la lista
 * 
 * @author Daniel Corroto Quirós
 *
 */
public class SimpleVCard {
	/**
	 * Uid Identificador del contacto
	 */
	private Uid uid;
	/**
	 * Nombre completo del contacto
	 */
	private FormattedName formattedName;

	/**
	 * Constructor sin parámetros
	 */
	public SimpleVCard() {
	}

	/**
	 * Constructor con los parámetros necesarios
	 * 
	 * @param uid
	 *            Identificador del contacto
	 * @param formattedName
	 *            Nombre completo del contacto
	 */
	public SimpleVCard(Uid uid, FormattedName formattedName) {
		super();
		this.uid = uid;
		this.formattedName = formattedName;
	}

	/**
	 * Constructor que obtiene los datos necesarios a partir del contacto
	 * 
	 * @param vcard
	 *            Información del contacto
	 */
	public SimpleVCard(VCard vcard) {
		super();
		this.uid = vcard.getUid();
		this.formattedName = vcard.getFormattedName();
	}

	/**
	 * Obtiene el identificador del contacto
	 * 
	 * @return Uid del contacto
	 */
	public Uid getUid() {
		return uid;
	}

	/**
	 * Establece el identificador del contacto
	 * 
	 * @param uid
	 *            Uid del contacto
	 */
	public void setUid(Uid uid) {
		this.uid = uid;
	}

	/**
	 * Obtiene el nombre completo del contacto
	 * 
	 * @return FormattedName del contacto
	 */
	public FormattedName getFormattedName() {
		return formattedName;
	}

	/**
	 * Establece el nombre completo del contacto
	 * 
	 * @param formattedName
	 *            FormattedName del contacto
	 */
	public void setFormattedName(FormattedName formattedName) {
		this.formattedName = formattedName;
	}

	/**
	 * Establece el nombre completo del contacto
	 * 
	 * @param name
	 *            Nombre completo del contacto
	 */
	public void setFormattedName(String name) {
		if (name != null) {
			this.formattedName = new FormattedName(name);
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SimpleVCard [uid=");
		builder.append(uid.getValue());
		builder.append(", formattedName=");
		builder.append(formattedName != null ? formattedName.getValue() : formattedName);
		builder.append("]");
		return builder.toString();
	}

}
