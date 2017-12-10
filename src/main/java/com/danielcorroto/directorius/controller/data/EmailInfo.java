package com.danielcorroto.directorius.controller.data;

import com.danielcorroto.directorius.controller.type.EmailTypeEnum;

/**
 * Información del email
 * 
 * @author Daniel Corroto Quirós
 *
 */
public class EmailInfo extends AbstractInfo {
	/**
	 * Dirección de email
	 */
	private String email;
	/**
	 * Tipo de email
	 */
	private EmailTypeEnum type;

	/**
	 * Constructor por defecto
	 */
	public EmailInfo() {
		super();
	}

	/**
	 * Constructor con parámetros
	 * 
	 * @param number
	 *            Dirección de email
	 * @param type
	 *            Tipo de email
	 * @param tag
	 *            Etiqueta
	 */
	public EmailInfo(String email, EmailTypeEnum type, String tag) {
		super();
		this.email = email;
		this.type = type;
		setTag(tag);
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public EmailTypeEnum getType() {
		return type;
	}

	public void setType(EmailTypeEnum type) {
		this.type = type;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EmailInfo [email=");
		builder.append(email);
		builder.append(", type=");
		builder.append(type);
		builder.append(", tag=");
		builder.append(getTag());
		builder.append("]");
		return builder.toString();
	}

}
