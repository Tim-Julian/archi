/**
  Copyright (c) 2012 Jean-Baptiste Sarrodie, France.

  Permission is hereby granted, free of charge, to any person
  obtaining a copy of this software and associated documentation
  files (the "Software"), to deal in the Software without
  restriction, including without limitation the rights to use,
  copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the
  Software is furnished to do so, subject to the following
  conditions:

  The above copyright notice and this permission notice shall be
  included in all copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
  OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
  HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
  OTHER DEALINGS IN THE SOFTWARE.
*/

package uk.ac.bolton.archimate.editor.diagram.figures.connections.roundedbendpoint;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import uk.ac.bolton.archimate.editor.preferences.IPreferenceConstants;
import uk.ac.bolton.archimate.editor.preferences.Preferences;

/**
 * @author Jean-Baptiste Sarrodie (aka Jaiguru)
 * 
 * Base on a example found on http://www.eclipse.org/forums/index.php/t/33583/
 * fully rewritten for Archi to work in all cases (any angle).
 */
public class RoundedPolylineConnection extends PolylineConnection {
	private double radius = 20;

	@Override
	protected void outlineShape(Graphics g) {
		boolean enabled = Preferences.STORE.getBoolean(IPreferenceConstants.USE_ROUNDED_CONNECTION);
		if (!enabled) {
			super.outlineShape(g);
			return;
		}
	
		PointList ps = getPoints();

		if (ps.size() == 0) {
			return;
		}
		
		// Start (bend)point
		Point src = ps.getPoint(0);
		
		for (int i = 1; i < ps.size(); i++) {
			// Current bendpoint
			Point bp = ps.getPoint(i);

			// If last bendpoint, draw connection
			if (i == ps.size() - 1) {
				g.drawLine(src, bp);
				continue;
			}

			// target bendpoint
			Point tgt = ps.getPoint(i + 1);
			
			// Switch to polar coordinates
			PolarPoint src_p = PolarPoint.point2PolarPoint(bp, src);
			PolarPoint tgt_p = PolarPoint.point2PolarPoint(bp, tgt);
			
			// Calculate arc angle between source and target
			// and be sure that arc angle is positive
			double arc = tgt_p.theta - src_p.theta;
			arc = (arc + 4.0*Math.PI) % (2.0*Math.PI);
			// Do we have to go from source to target or the opposite
			boolean src2tgt = arc < (Math.PI) ? true : false;
			arc = src2tgt ? arc : 2.0*Math.PI - arc;
			
			// Check dist against source and target
			double dist = radius;
			//dist = dist * (2 - 2 * arc / Math.PI);
			dist = Math.min(dist, src_p.r / 2.0);
			dist = Math.min(dist, tgt_p.r / 2.0);
			
			// Create ellipse approximation
			PolarPoint s_p;
			if (src2tgt) {
				s_p = src_p;
			} else {
				s_p = tgt_p;
			}
		
			// Find center of bendpoint arc
			PolarPoint center_p = new PolarPoint(dist, s_p.theta + arc / 2.0);
			
			// Compute source and target of bendpoint arc
			PolarPoint bpsrc_p = new PolarPoint(dist * Math.cos(arc / 2.0), src_p.theta);
			PolarPoint bptgt_p = new PolarPoint(dist * Math.cos(arc / 2.0), tgt_p.theta);
 
			// Switch back to rectangular coordinates
			Point center = center_p.toPoint().translate(bp);
			Point bpsrc = bpsrc_p.toPoint().translate(bp);
			Point bptgt = bptgt_p.toPoint().translate(bp);
			
			// Draw line
			g.drawLine(src, bpsrc);
			
			// Draw arc
			double arc_radius = dist * Math.sin(arc / 2.0);
			g.drawArc((int) Math.round(center.x - arc_radius),
					(int) Math.round(center.y - arc_radius),
					(int) Math.round(arc_radius * 2.0),
					(int) Math.round(arc_radius * 2.0), 
					(int) Math.round(to_deg((Math.PI + arc) / 2.0 + center_p.theta)),
					(int) Math.round(to_deg(Math.PI - arc)));
			
			// Prepare next iteration
			src = bptgt;
		}
	}

	private double to_deg(double rad) {
		return (rad * 180.0 / Math.PI);
	}
}
