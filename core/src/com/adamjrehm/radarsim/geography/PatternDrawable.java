package com.adamjrehm.radarsim.geography;

import com.adamjrehm.radarsim.config.Configuration;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class PatternDrawable extends Pattern{

    private static final Texture defaultPatternCrossTexture = new Texture("images/patterncross.png");

    public static final PatternDrawable PATTERN_ENTRY_ONE =
            new PatternDrawable(new Sprite(defaultPatternCrossTexture),
                    Configuration.getPatternEntryOneName(),
                    Configuration.getPatternEntryOneX(),
                    Configuration.getPatternEntryOneY());
    public static final PatternDrawable PATTERN_ENTRY_TWO =
            new PatternDrawable(new Sprite(defaultPatternCrossTexture),
                    Configuration.getPatternEntryTwoName(),
                    Configuration.getPatternEntryTwoX(),
                    Configuration.getPatternEntryTwoY());
    public static final PatternDrawable PATTERN_ENTRY_THREE =
            new PatternDrawable(new Sprite(defaultPatternCrossTexture),
                    Configuration.getPatternEntryThreeName(),
                    Configuration.getPatternEntryThreeX(),
                    Configuration.getPatternEntryThreeY());
    public static final PatternDrawable PATTERN_ENTRY_FOUR =
            new PatternDrawable(new Sprite(defaultPatternCrossTexture),
                    Configuration.getPatternEntryFourName(),
                    Configuration.getPatternEntryFourX(),
                    Configuration.getPatternEntryFourY());
    public static final PatternDrawable PATTERN_ENTRY_FIVE =
            new PatternDrawable(new Sprite(defaultPatternCrossTexture),
                    Configuration.getPatternEntryFiveName(),
                    Configuration.getPatternEntryFiveX(),
                    Configuration.getPatternEntryFiveY());

    private Sprite sprite;

    /**
     * Drawable pattern takes a sprite in constructor
     *
     * @param s Sprite to draw on the screen
     * @param name Name of the pattern point
     * @param x X coordinate of the pattern point
     * @param y Y coordinate of the pattern point
     */
    private PatternDrawable(Sprite s, String name, float x, float y){
        super(name, x, y);
        this.sprite = s;
        this.sprite.setPosition(x - (sprite.getWidth() / 2), y - (sprite.getWidth() / 2));
        this.sprite.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }

    public Sprite getSprite(){
        return this.sprite;
    }

    public static void drawPatternPoints(SpriteBatch batch){
        batch.draw(PATTERN_ENTRY_FIVE.sprite, PATTERN_ENTRY_FIVE.sprite.getX(), PATTERN_ENTRY_FIVE.sprite.getY());
        batch.draw(PATTERN_ENTRY_FOUR.sprite, PATTERN_ENTRY_FOUR.sprite.getX(), PATTERN_ENTRY_FOUR.sprite.getY());
        batch.draw(PATTERN_ENTRY_THREE.sprite, PATTERN_ENTRY_THREE.sprite.getX(), PATTERN_ENTRY_THREE.sprite.getY());
        batch.draw(PATTERN_ENTRY_TWO.sprite, PATTERN_ENTRY_TWO.sprite.getX(), PATTERN_ENTRY_TWO.sprite.getY());
        batch.draw(PATTERN_ENTRY_ONE.sprite, PATTERN_ENTRY_ONE.sprite.getX(), PATTERN_ENTRY_ONE.sprite.getY());
    }

    public static void dispose(){
        PATTERN_ENTRY_FIVE.getSprite().getTexture().dispose();
        PATTERN_ENTRY_FOUR.getSprite().getTexture().dispose();
        PATTERN_ENTRY_THREE.getSprite().getTexture().dispose();
        PATTERN_ENTRY_TWO.getSprite().getTexture().dispose();
        PATTERN_ENTRY_ONE.getSprite().getTexture().dispose();
    }
}
