package org.robovm.samples.contractr.ios.views;

import org.moe.natj.general.Pointer;
import org.moe.natj.general.ann.ByValue;
import org.moe.natj.general.ann.RegisterOnStartup;
import org.moe.natj.objc.ObjCRuntime;
import org.moe.natj.objc.ann.ObjCClassName;

import java.util.ArrayList;
import java.util.List;

import apple.coregraphics.opaque.CGContextRef;
import apple.coregraphics.struct.CGPoint;
import apple.coregraphics.struct.CGRect;
import apple.coregraphics.struct.CGSize;
import apple.foundation.NSCoder;
import apple.uikit.UIBezierPath;
import apple.uikit.UIColor;
import apple.uikit.UIView;

import static apple.coregraphics.c.CoreGraphics.CGContextAddEllipseInRect;
import static apple.coregraphics.c.CoreGraphics.CGContextSetFillColorWithColor;
import static apple.uikit.c.UIKit.UIGraphicsGetCurrentContext;

@org.moe.natj.general.ann.Runtime(ObjCRuntime.class)
@ObjCClassName("PieChartView")
@RegisterOnStartup
public class PieChartView extends UIView {

    public static native PieChartView alloc();

    protected PieChartView(Pointer peer) {
        super(peer);
    }

    @Override
    public UIView initWithCoder(NSCoder nsCoder) {
        return super.initWithCoder(nsCoder);
    }

    public static class Component {
        final UIColor color;
        final double value;

        public Component(UIColor color, double value) {
            this.color = color;
            this.value = value;
        }
    }

    private ArrayList<Component> components = new ArrayList<>();

    public void setComponents(List<Component> components) {
        this.components.clear();
        this.components.addAll(components);
        setNeedsDisplay();
    }

    @Override
    public void drawRect(@ByValue CGRect rect) {
        double diameter = Math.min(rect.size().width(), rect.size().height());
        double x = (rect.size().width() - diameter) / 2.0;
        double y = (rect.size().height() - diameter) / 2.0;
        double radius = diameter / 2.0;
        double originX = rect.size().width() / 2.0;
        double originY = rect.size().height() / 2.0;

        CGContextRef context = UIGraphicsGetCurrentContext();
        CGContextSetFillColorWithColor(context, UIColor.whiteColor().CGColor());
        CGContextAddEllipseInRect(context, new CGRect(new CGPoint(x, y), new CGSize(diameter, diameter)));

        /*
         * Draw fills and outlines separately otherwise the last fill will render over the outline of the first segment
         * the line witdh of that segment will not be as wide as expected.
         */
        drawArcs(radius, originX, originY, true);
        drawArcs(radius, originX, originY, false);
    }

    private void drawArcs(double radius, double originX, double originY, boolean fill) {
        double sum = 0.0;
        for (Component c : components) {
            sum += c.value;
        }

        double startAngle = -Math.PI / 2.0;
        for (int i = 0; i < components.size(); i++) {
            Component component = components.get(i);

            double endAngle = startAngle + 2.0 * Math.PI * component.value / sum;

            UIBezierPath path = UIBezierPath.alloc().init();
            path.moveToPoint(new CGPoint(originX, originY));
            path.addArcWithCenterRadiusStartAngleEndAngleClockwise(new CGPoint(originX, originY), radius, startAngle, endAngle, true);
            path.setLineWidth(2);

            if (fill) {
                /* Draw the filled arc. */
                component.color.setFill();
                path.fill();
            } else {
                /* Draw the white outline. */
                UIColor.whiteColor().setStroke();
                path.stroke();
            }

            startAngle = endAngle;
        }
    }
}
