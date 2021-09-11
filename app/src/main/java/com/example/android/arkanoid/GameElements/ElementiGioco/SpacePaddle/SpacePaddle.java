package com.example.android.arkanoid.GameElements.ElementiGioco.SpacePaddle;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.example.android.arkanoid.GameElements.ElementiGioco.Paddle;
import com.example.android.arkanoid.Util.ParamList;
import com.example.android.arkanoid.Util.SpriteUtil.AnimatedSprite;
import com.example.android.arkanoid.Util.SpriteUtil.Sprite;
import com.example.android.arkanoid.VectorMat.Vector2D;

public class SpacePaddle extends Paddle {
    private AnimatedSprite spriteRazzo;
    private Sprite spriteCannoni;


    public SpacePaddle(Vector2D position, Vector2D size, Vector2D speed, Sprite sprite, AnimatedSprite spriteRazzo, Sprite spriteCannoni) {
        super(position, size, speed, sprite);

        this.spriteRazzo = spriteRazzo;
        this.spriteRazzo.resizeImage(new Vector2D(size.getPosX(), size.getPosX()));

        this.spriteCannoni = spriteCannoni;
        this.spriteCannoni.resizeImage(new Vector2D(size.getPosX() / 4, size.getPosX() / 4));
    }

    @Override
    public void setup(int screenWidth, int screenHeight, ParamList params) {
        super.setup(screenWidth, screenHeight, params);
    }

    @Override
    public void logica(float dt, int screenWidth, int screenHeight, ParamList params) {
        if(Math.abs( this.targetX - this.position.getPosX() ) > this.TOLLERANZA){
            //Se la tolleranza non è rispettata allora sposta il paddle
            if(this.targetX > this.position.getPosX())
                //Se va a destra
                this.setDirection(new Vector2D(1, 0));
            else
                this.setDirection(new Vector2D(-1, 0));
            Vector2D nextStep = this.getNextStep(dt);

            //Controllo della collisione con lo schermo
            if( nextStep.getPosX() - (this.getSize().getPosX() * 0.5) > 0 && nextStep.getPosX() + (this.getSize().getPosX() * 0.5) < screenWidth ){
                //Se non collide con lo schermo controlla la collisione con la palla

                this.setPosition(nextStep);
            }
        }
    }

    /**
     * Restituisce la posizione da cui i fucili sparano
     * @return Restituisce un'array di due lementi
     */
    public Vector2D[] getSpotCannoni(){
        Vector2D[] esito = new Vector2D[2];

        float posYCannone = this.position.getPosY() - this.size.prodottoPerScalare(0.5f).getPosY() - this.spriteCannoni.getHeight() + 10;
        float posXCannone1 = this.position.getPosX() - this.size.prodottoPerScalare(0.5f).getPosX() + (this.spriteCannoni.getWidth() * 0.5f) - 5;
        float posXCannone2 = this.position.getPosX() + this.size.prodottoPerScalare(0.5f).getPosX() - (this.spriteCannoni.getWidth() * 0.5f) + 5;

        esito[0] = new Vector2D(posXCannone1, posYCannone);
        esito[1] = new Vector2D(posXCannone2, posYCannone);

        return esito;
    }

    @Override
    public void drawEntity(Canvas canvas, Paint paint) {
        float posXRazzo = this.position.getPosX();
        float posYRazzo = this.position.getPosY() + (this.sprite.getHeight() * 0.5f) + (this.spriteRazzo.getSingleHeight() * 0.5f) - 10;
        this.spriteRazzo.drawSprite((int)posXRazzo, (int)posYRazzo, canvas, paint);

        float posYCannone = this.position.getPosY() - this.size.prodottoPerScalare(0.5f).getPosY() - (this.spriteCannoni.getHeight() * 0.5f) + 10;
        float posXCannone1 = this.position.getPosX() - this.size.prodottoPerScalare(0.5f).getPosX() + (this.spriteCannoni.getWidth() * 0.5f) - 5;
        float posXCannone2 = this.position.getPosX() + this.size.prodottoPerScalare(0.5f).getPosX() - (this.spriteCannoni.getWidth() * 0.5f) + 5;

        this.spriteCannoni.drawSprite((int)posXCannone1, (int)posYCannone, canvas, paint);
        this.spriteCannoni.drawSprite((int)posXCannone2, (int)posYCannone, canvas, paint);

        super.drawEntity(canvas, paint);
    }
}
