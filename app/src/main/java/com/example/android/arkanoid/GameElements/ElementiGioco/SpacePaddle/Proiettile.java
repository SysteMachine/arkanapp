package com.example.android.arkanoid.GameElements.ElementiGioco.SpacePaddle;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.example.android.arkanoid.GameElements.ElementiBase.AbstractScene;
import com.example.android.arkanoid.GameElements.ElementiBase.Entity;
import com.example.android.arkanoid.Util.ParamList;
import com.example.android.arkanoid.VectorMat.Vector2D;

public class Proiettile extends Entity {
    public final static String EVENTO_RIMOZIONE_PROIETTILE = "PROIETTILE_RIMOZIONE_PROIETTILE";
    public final static String EVENTO_COLPO_ASTEROIDE = "PROIETTILE_COLPO_ASTEROIDE";
    public final static String EVENTO_COLPO_BRICK = "PROIETTILE_COLPO_BRICK";

    public Proiettile(Vector2D position, Vector2D speed) {
        super(
                "proiettile",
                position,
                new Vector2D(0, -1),
                new Vector2D(10, 10),
                speed,
                null
        );
    }

    /**
     * Controlla la collisione del proiettile con un asteroide
     * @param scena Scena
     */
    private void controlloCollisioneAsteroide(AbstractScene scena){
        Entity[] entita = scena.getEntityByName("asteroide");
        for(Entity e : entita){
            Asteroide a = (Asteroide)e;
            if(a.getBounds().intersect(this.getBounds())){
                //Se l'asteroide colpisce il proiettile
                ParamList parametri = new ParamList();
                parametri.add("asteroide", a);
                scena.sendEvent(Proiettile.EVENTO_COLPO_ASTEROIDE, parametri);

                parametri = new ParamList();
                parametri.add("proiettile", this);
                scena.sendEvent(Proiettile.EVENTO_RIMOZIONE_PROIETTILE, parametri);

                break;
            }
        }
    }

    /**
     * Controlla la collisione del proiettile con un brick
     * @param scena Scena
     */
    private void controlloCollisioneBrick(AbstractScene scena){
        Entity[] entita = scena.getEntityByName("brick");
        for(Entity e : entita){
            SpaceBrick sb = (SpaceBrick)e;
            if(sb.getBounds().intersect(this.getBounds())){
                sb.decrementaVita();
                sb.shake(200, 60);

                ParamList parametri = new ParamList();
                parametri.add("brick", sb);
                scena.sendEvent(Proiettile.EVENTO_COLPO_BRICK, parametri);

                parametri = new ParamList();
                parametri.add("proiettile", this);
                scena.sendEvent(Proiettile.EVENTO_RIMOZIONE_PROIETTILE, parametri);

                break;
            }
        }
    }

    @Override
    public void logica(float dt, int screenWidth, int screenHeight, ParamList params) {
        Vector2D nextStep = this.getNextStep(dt);
        this.setPosition(nextStep);

        AbstractScene scena = params.get(AbstractScene.PARAMETRO_SCENA);
        if(scena != null){
            ParamList parametri = new ParamList();
            parametri.add("proiettile", this);

            if(this.getPosition().getPosY() < -this.getSize().getPosY() * 0.5)
                scena.sendEvent(Proiettile.EVENTO_RIMOZIONE_PROIETTILE, parametri);

            this.controlloCollisioneAsteroide(scena);
            this.controlloCollisioneBrick(scena);
        }
    }

    @Override
    public void drawEntity(Canvas canvas, Paint paint) {
        paint.setColor(Color.rgb(255, 0, 255));
        paint.setStyle(Paint.Style.FILL);

        canvas.drawOval(this.getBounds(), paint);
    }
}
