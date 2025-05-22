package com.dei.cafeteria.vista;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.*;

/**
 * La clase TextPrompt mostrará un texto de ayuda (prompt) sobre un componente de texto cuando
 * el Document del campo de texto esté vacío. La propiedad Show se utiliza para
 * determinar la visibilidad del texto de ayuda.
 *
 * La Fuente y el Color del texto de ayuda heredarán por defecto las propiedades
 * del componente de texto padre. Estos valores pueden modificarse libremente después
 * de la construcción de la clase.
 */
public class TextPrompt extends JLabel implements FocusListener, DocumentListener {
    private static final long serialVersionUID = 1L;

    public enum Show {
        ALWAYS,      // SIEMPRE
        FOCUS_GAINED,// AL_OBTENER_FOCO
        FOCUS_LOST;  // AL_PERDER_FOCO
    }

    private JTextComponent component;
    private Document document;

    private Show show;
    private boolean showPromptOnce;    // mostrarPromptUnaVez
    private int focusLost;             // vecesQuePerdioFoco

    public TextPrompt(String text, JTextComponent component) {
        this(text, component, Show.ALWAYS);
    }

    public TextPrompt(String text, JTextComponent component, Show show) {
        this.component = component;
        setShow(show);
        document = component.getDocument();

        setText(text);
        setFont(component.getFont());
        setForeground(component.getForeground());
        setBorder(new EmptyBorder(component.getInsets()));
        setHorizontalAlignment(JLabel.LEADING);

        component.addFocusListener(this);
        document.addDocumentListener(this);

        component.setLayout(new BorderLayout());
        component.add(this);
        checkForPrompt();
    }

    /**
     * Método de conveniencia para cambiar el valor alfa del Color
     * de primer plano actual al valor especificado.
     *
     * @param alpha valor en el rango de 0 - 1.0.
     */
    public void changeAlpha(float alpha) {
        changeAlpha((int) (alpha * 255));
    }

    /**
     * Método de conveniencia para cambiar el valor alfa del Color
     * de primer plano actual al valor especificado.
     *
     * @param alpha valor en el rango de 0 - 255.
     */
    public void changeAlpha(int alpha) {
        alpha = alpha > 255 ? 255 : alpha < 0 ? 0 : alpha;

        Color foreground = getForeground();
        int red = foreground.getRed();
        int green = foreground.getGreen();
        int blue = foreground.getBlue();

        Color withAlpha = new Color(red, green, blue, alpha);
        super.setForeground(withAlpha);
    }

    /**
     * Método de conveniencia para cambiar el estilo de la Fuente actual. Los valores
     * de estilo se encuentran en la clase Font. Valores comunes pueden ser: Font.BOLD,
     * Font.ITALIC y Font.BOLD + Font.ITALIC.
     *
     * @param style valor que representa el nuevo estilo de la Fuente.
     */
    public void changeStyle(int style) {
        setFont(getFont().deriveFont(style));
    }

    /**
     * Obtiene la propiedad Show
     *
     * @return la propiedad Show.
     */
    public Show getShow() {
        return show;
    }

    /**
     * Establece la propiedad Show del prompt para controlar cuándo se muestra. Los valores
     * válidos son:
     *
     * Show.ALWAYS (por defecto) - siempre muestra el prompt
     * Show.Focus_GAINED - muestra el prompt cuando el componente obtiene el foco
     * (y lo oculta cuando pierde el foco)
     * Show.Focus_LOST - muestra el prompt cuando el componente pierde el foco
     * (y lo oculta cuando obtiene el foco)
     *
     * @param show un enum Show válido
     */
    public void setShow(Show show) {
        this.show = show;
    }

    /**
     * Obtiene la propiedad showPromptOnce
     *
     * @return la propiedad showPromptOnce.
     */
    public boolean getShowPromptOnce() {
        return showPromptOnce;
    }

    /**
     * Muestra el prompt una sola vez. Una vez que el componente ha ganado/perdido
     * el foco una vez, el prompt no se mostrará nuevamente.
     *
     * @param showPromptOnce cuando es true el prompt solo se mostrará una vez,
     *                       de lo contrario se mostrará repetidamente.
     */
    public void setShowPromptOnce(boolean showPromptOnce) {
        this.showPromptOnce = showPromptOnce;
    }

    /**
     * Verifica si el prompt debe ser visible o no. La visibilidad cambiará
     * con las actualizaciones del Document y con los cambios de foco.
     */
    private void checkForPrompt() {
        // Text has been entered, remove the prompt
        if (document.getLength() > 0) {
            setVisible(false);
            return;
        }

        // Prompt has already been shown once, remove it
        if (showPromptOnce && focusLost > 0) {
            setVisible(false);
            return;
        }

        // Check the Show property and component focus to determine if the
        // prompt should be displayed.
        if (component.hasFocus()) {
            if (show == Show.ALWAYS || show == Show.FOCUS_GAINED)
                setVisible(true);
            else
                setVisible(false);
        } else {
            if (show == Show.ALWAYS || show == Show.FOCUS_LOST)
                setVisible(true);
            else
                setVisible(false);
        }
    }

    // Implement FocusListener
    public void focusGained(FocusEvent e) {
        checkForPrompt();
    }

    public void focusLost(FocusEvent e) {
        focusLost++;
        checkForPrompt();
    }

    // Implement DocumentListener
    public void insertUpdate(DocumentEvent e) {
        checkForPrompt();
    }

    public void removeUpdate(DocumentEvent e) {
        checkForPrompt();
    }

    public void changedUpdate(DocumentEvent e) {
    }
}