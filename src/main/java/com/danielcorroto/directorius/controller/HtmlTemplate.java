package com.danielcorroto.directorius.controller;

/**
 * Plantilla para la sustitución de cadenas
 * 
 * @author Daniel Corroto Quirós
 *
 */
public interface HtmlTemplate {
	/**
	 * Ruta del CSS de Bootstrap
	 */
	public static final String CSS_BOOTSTRAP = "##CSS_BOOTSTRAP##";
	/**
	 * Ruta del CSS de los estilos del contacto
	 */
	public static final String CSS_VCARD = "##CSS_VCARD##";

	/**
	 * Agrupación de direcciones postales (cargar de otro fichero, opcional)
	 */
	public static final String GROUP_ADDRESS = "##GROUP_ADDRESS##";
	/**
	 * Agrupación de categorías (cargar de otro fichero, opcional)
	 */
	public static final String GROUP_CATEGORY = "##GROUP_CATEGORY##";
	/**
	 * Agrupación de correos electrónicos (cargar de otro fichero, opcional)
	 */
	public static final String GROUP_EMAIL = "##GROUP_EMAIL##";
	/**
	 * Agrupación de teléfono (cargar de otro fichero, opcional)
	 */
	public static final String GROUP_PHONE = "##GROUP_PHONE##";

	/**
	 * Sección de direcciones postales (cargar de otro fichero)
	 */
	public static final String SECTION_ADDRESS = "##SECTION_ADDRESS##";
	/**
	 * Sección de categorías (cargar de otro fichero)
	 */
	public static final String SECTION_CATEGORY = "##SECTION_CATEGORY##";
	/**
	 * Sección de correos electrónicos (cargar de otro fichero)
	 */
	public static final String SECTION_EMAIL = "##SECTION_EMAIL##";
	/**
	 * Sección de teléfono (cargar de otro fichero)
	 */
	public static final String SECTION_PHONE = "##SECTION_PHONE##";
	/**
	 * Sección de fotografía (cargar de otro fichero)
	 */
	public static final String SECTION_PHOTO = "##SECTION_PHOTO##";

	/**
	 * URL o data de la foto
	 */
	public static final String PHOTO = "##PHOTO##";
	/**
	 * Para data de la foto, el color de fondo
	 */
	public static final String PHOTO_COLOR = "##PHOTO_COLOR##";
	/**
	 * Para data de la foto, las iniciales del nombre
	 */
	public static final String PHOTO_INITIAL = "##PHOTO_INITIAL##";

	/**
	 * Nombre completo del contacto
	 */
	public static final String FULL_NAME = "##FULL_NAME##";
	/**
	 * Fecha del cumpleaños
	 */
	public static final String BIRTHDAY = "##BIRTHDAY##";
	/**
	 * Notas
	 */
	public static final String NOTES = "##NOTES##";
	/**
	 * Categoría
	 */
	public static final String CATEGORY = "##CATEGORY##";

	/**
	 * Número de teléfono
	 */
	public static final String PHONE = "##PHONE##";
	/**
	 * Tipo de teléfono
	 */
	public static final String PHONE_TYPE = "##PHONE_TYPE##";
	/**
	 * Texto/descripción del teléfono
	 */
	public static final String PHONE_TEXT = "##PHONE_TEXT##";

	/**
	 * Dirección del correo electrónico
	 */
	public static final String EMAIL = "##EMAIL##";
	/**
	 * Tipo de dirección del correo electrónico
	 */
	public static final String EMAIL_TYPE = "##EMAIL_TYPE##";
	/**
	 * Texto/descripción del correo electrónico
	 */
	public static final String EMAIL_TEXT = "##EMAIL_TEXT##";

	/**
	 * Dirección postal
	 */
	public static final String ADDRESS = "##ADDRESS##";
	/**
	 * Tipo de la dirección postal
	 */
	public static final String ADDRESS_TYPE = "##ADDRESS_TYPE##";
	/**
	 * Texto/descripción de la dirección postal
	 */
	public static final String ADDRESS_TEXT = "##ADDRESS_TEXT##";
	
	/**
	 * Fecha de última edición
	 */
	public static final String LAST_EDITION = "##LAST_EDITION##";
}
