import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JFrame;
import javax.swing.JLabel;

class Toast extends JFrame {
    public Toast(final String message) {
        setUndecorated(true);
        setLayout(new GridBagLayout());
        setBackground(new Color(240,240,240,250));
        setLocationRelativeTo(null);
        setSize(300, 50);
        setLocation(140, 425);
        add(new JLabel(message));

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                setShape(new  RoundRectangle2D.Double(0,0,getWidth(),
                        getHeight(), 20, 20));
            }
        });
    }

    public void display() {
        try {
            setOpacity(1);
            setVisible(true);
            Thread.sleep(1000);

            //hide the toast message in slow motion
            for (double d = 1.0; d > 0.2; d -= 0.1) {
                Thread.sleep(50);
                setOpacity((float)d);
            }

            // set the visibility to false
            setVisible(false);
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}