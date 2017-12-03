package com.danielcorroto.directorius.view;

import javafx.scene.control.MenuItem;

/**
 * Colección de todos los items
 * 
 * @author Daniel Corroto Quirós
 *
 */
public class MenuItems {
	/**
	 * Fichero / Nuevo
	 */
	private MenuItem fileNew;
	/**
	 * Fichero / Abrir
	 */
	private MenuItem fileOpen;
	/**
	 * Fichero / Salir
	 */
	private MenuItem fileExit;
	/**
	 * Contacto / Añadir
	 */
	private MenuItem contactAdd;
	/**
	 * Contacto / Editar
	 */
	private MenuItem contactEdit;
	/**
	 * Contacto / Eliminar
	 */
	private MenuItem contactRemove;
	/**
	 * Cumpleaños / Hoy
	 */
	private MenuItem birthdayToday;
	/**
	 * Cumpleaños / Durante una semana
	 */
	private MenuItem birthdayWithinWeek;
	/**
	 * Cumpleaños / Durante un mes
	 */
	private MenuItem birthdayWithinMonth;
	/**
	 * Cumpleaños / Esta semana
	 */
	private MenuItem birthdayThisWeek;
	/**
	 * Cumpleaños / Este mes
	 */
	private MenuItem birthdayThisMonth;
	/**
	 * Ayuda / Acerca de...
	 */
	private MenuItem helpAbout;

	public MenuItem getFileNew() {
		return fileNew;
	}

	public void setFileNew(MenuItem fileNew) {
		this.fileNew = fileNew;
	}

	public MenuItem getFileOpen() {
		return fileOpen;
	}

	public void setFileOpen(MenuItem fileOpen) {
		this.fileOpen = fileOpen;
	}

	public MenuItem getFileExit() {
		return fileExit;
	}

	public void setFileExit(MenuItem fileExit) {
		this.fileExit = fileExit;
	}

	public MenuItem getContactAdd() {
		return contactAdd;
	}

	public void setContactAdd(MenuItem contactAdd) {
		this.contactAdd = contactAdd;
	}

	public MenuItem getContactEdit() {
		return contactEdit;
	}

	public void setContactEdit(MenuItem contactEdit) {
		this.contactEdit = contactEdit;
	}

	public MenuItem getContactRemove() {
		return contactRemove;
	}

	public void setContactRemove(MenuItem contactRemove) {
		this.contactRemove = contactRemove;
	}

	public MenuItem getBirthdayToday() {
		return birthdayToday;
	}

	public void setBirthdayToday(MenuItem birthdayToday) {
		this.birthdayToday = birthdayToday;
	}

	public MenuItem getBirthdayWithinWeek() {
		return birthdayWithinWeek;
	}

	public void setBirthdayWithinWeek(MenuItem birthdayWithinWeek) {
		this.birthdayWithinWeek = birthdayWithinWeek;
	}

	public MenuItem getBirthdayWithinMonth() {
		return birthdayWithinMonth;
	}

	public void setBirthdayWithinMonth(MenuItem birthdayWithinMonth) {
		this.birthdayWithinMonth = birthdayWithinMonth;
	}

	public MenuItem getBirthdayThisWeek() {
		return birthdayThisWeek;
	}

	public void setBirthdayThisWeek(MenuItem birthdayThisWeek) {
		this.birthdayThisWeek = birthdayThisWeek;
	}

	public MenuItem getBirthdayThisMonth() {
		return birthdayThisMonth;
	}

	public void setBirthdayThisMonth(MenuItem birthdayThisMonth) {
		this.birthdayThisMonth = birthdayThisMonth;
	}

	public MenuItem getHelpAbout() {
		return helpAbout;
	}

	public void setHelpAbout(MenuItem helpAbout) {
		this.helpAbout = helpAbout;
	}

}
