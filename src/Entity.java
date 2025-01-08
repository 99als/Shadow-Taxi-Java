import bagel.*;
import java.util.Properties;

// Entity class to represent game entities
abstract class Entity {
    //Variables
    private double x, y;
    private Image image;
    protected double health;
    protected Image smoke = new Image("res/smoke.png");
    protected Image blood = new Image("res/blood.png");
    private final Properties game_props = IOUtils.readPropertiesFile("res/app.properties");

    /**
     * Constructor for the Entity class.
     * Initializes the position and image of the entity.
     *
     * @param x     The x-coordinate of the entity.
     * @param y     The y-coordinate of the entity.
     * @param image The image representing the entity.
     */
    public Entity(double x, double y, Image image) {
        this.x = x;
        this.y = y;
        this.image = image;
    }

    //Getters/Setters
    /**
     * Gets the current x-coordinate of the object.
     *
     * @return the x-coordinate
     */
    public double getX() {
        return x;
    }
    /**
     * Sets the x-coordinate of the object.
     *
     * @param x the new x-coordinate
     */
    public void setX(double x) {
        this.x = x;
    }
    /**
     * Gets the current y-coordinate of the object.
     *
     * @return the y-coordinate
     */
    public double getY() {
        return y;
    }
    /**
     * Sets the y-coordinate of the object.
     *
     * @param y the new y-coordinate
     */
    public void setY(double y) {
        this.y = y;
    }
    /**
     * Gets the image associated with the object.
     *
     * @return the image
     */
    public Image getImage() {
        return image;
    }
    /**
     * Sets the image associated with the object.
     *
     * @param image the new image
     */
    public void setImage(Image image) {
        this.image = image;
    }
    /**
     * Gets the game properties file.
     *
     * @return the game properties
     */
    public Properties getGameProps() {
        return game_props;
    }
    /**
     * Gets the current health of the object.
     *
     * @return the current health
     */
    public double getHealth() {
        return health;
    }
    /**
     * Sets the health of the object.
     *
     * @param health the new health value
     */
    public void setHealth(double health) {
        this.health = health;
    }
    /**
     * Reduces the object's health by a specified amount.
     * The health will not drop below 0.
     *
     * @param amount the amount of damage to apply
     */
    public void damage(double amount) {
        this.health = Math.max(0, this.health - amount);
    }
    /**
     * Checks if the object's health is depleted (i.e., the object is destroyed).
     *
     * @return true if the object is destroyed, false otherwise
     */
    public boolean isDestroyed() {
        return health <= 0;
    }

    /**
     * Draws the image at the current coordinates (x, y).
     * <p>
     * This method uses the image associated with the object and renders it
     * at the specified x and y coordinates on the screen.
     * </p>
     */
    public void draw() {
        image.draw(x, y);
    }

    /**
     * Abstract method to update the entity's state.
     * Must be implemented by subclasses.
     *
     * @param input The current input from the keyboard or mouse.
     */
    public abstract void update(Input input);
}
