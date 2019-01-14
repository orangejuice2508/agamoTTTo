package de.gruppe2.agamoTTTo.controller;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

/**
 * Every controller extends this class and therefore inherits all his functionality.
 */
public abstract class BaseController {

    /**
     * This method removes leading and trailing spaces in form inputs.
     *
     * @param webDataBinder binds web request parameters to concrete JAVA objects
     */
    @InitBinder
    public void trimWhiteSpacesFromInputs(WebDataBinder webDataBinder){
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(Boolean.TRUE);

        webDataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }
}
