#! /usr/bin/env python
import pygtk
pygtk.require('2.0')
import gtk, cairo, math

# Create a GTK+ widget on which we will draw using Cairo
class Screen(gtk.DrawingArea):

    # Draw in response to an expose-event
    __gsignals__ = { "expose-event": "override" }

    # Handle the expose-event by drawing
    def do_expose_event(self, event):

        # Create the cairo context
        cr = self.window.cairo_create()

        # Restrict Cairo to the exposed area; avoid extra work
        cr.rectangle(event.area.x, event.area.y,
                event.area.width, event.area.height)
        cr.clip()

        self.draw(cr, *self.window.get_size())

    def draw(self, ctx, width, height):
	_min = min(width, height)
        ctx.scale (_min/1.0, _min/1.0)
	_h=height/1.0/_min
	_w=width/1.0/_min
        
        #ctx.set_source_rgb (1, 1, 1)
        ctx.set_line_width (1.0/_min)
	
        ctx.move_to(0.5,0.1)
	ctx.line_to(0.9,0.5)
	ctx.line_to(0.5,0.9)
	ctx.line_to(0.1,0.5)
	ctx.close_path()
	ctx.stroke()
        ctx.set_line_width (5.0/_min)
	ctx.move_to(0.5,0.3)
	ctx.line_to(0.5,0.7)
	ctx.move_to(0.3,0.5)
	ctx.line_to(0.7,0.5)
	ctx.stroke()

# GTK mumbo-jumbo to show the widget in a window and quit when it's closed
def run(Widget):
    window = gtk.Window(gtk.WINDOW_POPUP)
    screen = window.get_screen()
    window.resize(screen.get_width(), screen.get_height())
    window.connect("delete-event", gtk.main_quit)
    window.move(0,0)
    window.set_events(window.get_events() | gtk.gdk.BUTTON_PRESS_MASK)
    window.connect("button_press_event", gtk.main_quit)
    widget = Widget()
    widget.show()
    window.add(widget)
    window.present()
    gtk.main()

if __name__ == "__main__":
    run(Screen)
