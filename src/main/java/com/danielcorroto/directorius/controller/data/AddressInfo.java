package com.danielcorroto.directorius.controller.data;

import com.danielcorroto.directorius.controller.type.AddressTypeEnum;

/**
 * Información de dirección
 * 
 * @author Daniel Corroto Quirós
 *
 */
public class AddressInfo extends AbstractInfo {
	/**
	 * Calle
	 */
	private String street;
	/**
	 * Localidad
	 */
	private String locality;
	/**
	 * Región / Estado / Provincia
	 */
	private String region;
	/**
	 * Código postal
	 */
	private String postalCode;
	/**
	 * País
	 */
	private String country;
	/**
	 * Tipo de dirección
	 */
	private AddressTypeEnum type;

	/**
	 * Constructor por defecto
	 */
	public AddressInfo() {
		super();
	}

	/**
	 * Constructor con parámetros
	 * 
	 * @param street
	 *            Calle
	 * @param locality
	 *            Localidad
	 * @param region
	 *            Región / Estado / Provincia
	 * @param postalCode
	 *            Código postal
	 * @param country
	 *            País
	 * @param type
	 *            Tipo de dirección
	 * @param tag
	 *            Etiqueta
	 */
	public AddressInfo(String street, String locality, String region, String postalCode, String country, AddressTypeEnum type, String tag) {
		super();
		this.street = street;
		this.locality = locality;
		this.region = region;
		this.postalCode = postalCode;
		this.country = country;
		this.type = type;
		setTag(tag);
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public AddressTypeEnum getType() {
		return type;
	}

	public void setType(AddressTypeEnum type) {
		this.type = type;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AddressInfo [street=");
		builder.append(street);
		builder.append(", locality=");
		builder.append(locality);
		builder.append(", region=");
		builder.append(region);
		builder.append(", postalCode=");
		builder.append(postalCode);
		builder.append(", country=");
		builder.append(country);
		builder.append(", type=");
		builder.append(type);
		builder.append(", tag=");
		builder.append(getTag());
		builder.append("]");
		return builder.toString();
	}

}
