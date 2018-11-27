/**
 * A wrapper class that constructs and initilize the GUI
 * @author cyrusvillacampa
 *
 */
public class GUIConstructor {
    public GUIConstructor(UIEventQueue uiEventQueue,GUIMapRendering GuiMapRendering) {
        init(uiEventQueue, GuiMapRendering);
    }
    
    private void init(UIEventQueue uiEventQueue,GUIMapRendering GuiMapRendering) {
        GUIPage gui = new GUIPage(uiEventQueue, GuiMapRendering);
        gui.setVisible(true);
    }
}
