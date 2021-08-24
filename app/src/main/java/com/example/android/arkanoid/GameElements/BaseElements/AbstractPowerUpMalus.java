package com.example.android.arkanoid.GameElements.BaseElements;

import com.example.android.arkanoid.GameCore.GameLoop;
import com.example.android.arkanoid.GameElements.Paddle;
import com.example.android.arkanoid.GameElements.SceneDefinite.ModalitaClassica;
import com.example.android.arkanoid.Util.ParamList;
import com.example.android.arkanoid.Util.SpriteUtil.Sprite;
import com.example.android.arkanoid.VectorMat.Vector2D;

public class AbstractPowerUpMalus extends AbstractEntity implements PowerUpMalusInterface{
    protected Alterazione alterazione;
    protected Sprite sprite;

    public AbstractPowerUpMalus(Vector2D posizione, Vector2D size, float velocita, Alterazione alterazione, int idSprite, GameLoop gameLoop){
        this.alterazione = alterazione;
        this.sprite = new Sprite(idSprite, gameLoop);

        this.setPosition(posizione);
        this.setSize(size);
        this.setSpeed(velocita);
        this.setDirection(new Vector2D(0, 1));
    }

    @Override
    public void setSize(Vector2D size) {
        super.setSize(size);
        this.sprite.resizeImage(size);
    }

    @Override
    public void logica(float dt, int screenWidth, int screenHeight, ParamList params) {
        this.position = Vector2D.sommaVettoriale(this.position, this.direction.prodottoPerScalare(this.speed * dt));
        AbstractScene scena = params.get(AbstractScene.SCENA);
        if(scena != null){
            Paddle paddle = scena.getFirstEntityByName("Paddle");
            ParamList esito = new ParamList();
            esito.add("powerup", this);
            if(paddle != null && paddle.getBounds().intersect(this.getBounds()))
                scena.sendEvent(ModalitaClassica.EVENTO_POWERUP, esito);
            if(this.position.getPosY() - (this.size.getPosY() * 0.5f) > screenHeight)
                scena.sendEvent(ModalitaClassica.EVENTO_RIMOZIONE_POWERUP, esito);
        }
    }

    @Override
    public Alterazione getAlterazione() {
        return this.alterazione;
    }

    @Override
    public Sprite getPowerUpMalusImage() {
        return this.sprite;
    }
}
