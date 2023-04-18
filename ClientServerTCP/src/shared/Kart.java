package shared;

import java.awt.*;
import java.awt.geom.*;
import java.io.Serializable;

public class Kart implements Serializable
{
    private Color color;
   private double x, y, size;
   
   public Kart(double a, double b, double s, Color c)
   {
        x = a;
        y = b;
        size = s;
        color = c;

   }

   public void drawSprite (Graphics2D g2d)
   {
      Rectangle2D.Double square = new Rectangle2D.Double(x, y, size, size);
        g2d.setColor(color);
        g2d.fill(square);
   }

   public void moveH (double n){
        x += n;
   }

    public void moveV (double n){
          y += n;
    }

    public double setX (double n){
        x = n;
        return x;
    }

    public double setY (double n){
        y = n;
        return y;
    }

    public double getX (){
        return x;
    }

    public double getY (){
        return y;
    }
}