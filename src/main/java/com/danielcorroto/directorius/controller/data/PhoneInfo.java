package com.danielcorroto.directorius.controller.data;

import com.danielcorroto.directorius.controller.type.PhoneTypeEnum;

/**
 * Información del teléfono
 * 
 * @author Daniel Corroto Quirós
 *
 */
public class PhoneInfo extends AbstractInfo{
	/**
	 * Número de teléfono
	 */
	private String number;
	/**
	 * Tipo de teléfono
	 */
	private PhoneTypeEnum type;

	/**
	 * Constructor por defecto
	 */
	public PhoneInfo() {
		super();
	}

	/**
	 * Constructor con parámetros
	 * 
	 * @param number
	 *            Número de teléfono
	 * @param type
	 *            Tipo de teléfono
	 * @param tag
	 *            Etiqueta
	 */
	public PhoneInfo(String number, PhoneTypeEnum type, String tag) {
		super();
		this.number = number;
		this.type = type;
		setTag(tag);
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public PhoneTypeEnum getType() {
		return type;
	}

	public void setType(PhoneTypeEnum type) {
		this.type = type;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PhoneInfo [number=");
		builder.append(number);
		builder.append(", type=");
		builder.append(type);
		builder.append(", tag=");
		builder.append(getTag());
		builder.append("]");
		return builder.toString();
	}

}
