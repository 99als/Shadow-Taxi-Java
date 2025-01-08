import bagel.*;

class TripEndFlag extends Entity {
    //Flags
    private boolean tripCompleted = false;
    //Flag constants
    private final int TRIP_MOVE_SPEED = Integer.parseInt(getGameProps().getProperty("gameObjects.taxi.speedY"));

    /**
     * Constructs a TripEndFlag at the passenger's destination coordinates.
     * The flag is placed at the x-coordinate of the passenger's destination and
     * the y-coordinate is calculated based on the passenger's starting y-distance.
     *
     * @param passenger the passenger associated with this trip
     * @param image the image representing the trip end flag
     */
    public TripEndFlag(Passenger passenger, Image image) {
        super(passenger.getEndX(), passenger.getY() - passenger.getyDistance(), image);
    }
    /**
     * Sets the trip completion status.
     * Once the trip is completed, the flag will not be rendered.
     *
     * @param tripCompleted true if the trip is completed, false otherwise
     */
    public void setTripCompleted(boolean tripCompleted) {
        this.tripCompleted = tripCompleted;
    }

    /**
     * Updates the position of the trip end flag based on user input.
     * If the UP key is pressed, the flag will move downwards.
     *
     * @param input the user input for controlling the flag's movement
     */
    @Override
    public void update(Input input) {
        if (input.isDown(Keys.UP)) {
            setY(getY() + TRIP_MOVE_SPEED);
        }
    }

    /**
     * Draws the trip end flag on the screen, unless the trip has been completed.
     * If the trip is completed, the flag will not be rendered.
     */
    @Override
    public void draw() {
        if (tripCompleted) {
            return;
        }
        getImage().draw(getX(), getY());
    }
}
