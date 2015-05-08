package fi.solita.botsofbf.graph;

import fi.solita.botsofbf.BotController;

/**
 * Created by henriny on 08/05/15.
 */
public class MoveTranslator {

    public static BotController.Move translate(final Node whereAmI, final Route route) {
        final Node targeNode = GraphReader.getMapCache().get(route.to);
        if(whereAmI.x < targeNode.x) return BotController.Move.RIGHT;
        if(whereAmI.x > targeNode.x) return BotController.Move.LEFT;
        if(whereAmI.y < targeNode.y) return BotController.Move.UP;
        if(whereAmI.y > targeNode.y) return BotController.Move.DOWN;
        throw new IllegalArgumentException("No moves available");
    }

}
