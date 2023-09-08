package city.windmill.cropchance.command;

public class CropCommand extends BasicCommand {
    public CropCommand() {
        super("crop");
        addChild(new InfoCommand());
        addChild(new CropCardCommand());
        addChild(new CrossCommand());
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
