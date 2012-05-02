#!/usr/bin/env python

import gobject

import dbus
import dbus.service
import dbus.mainloop.glib

class Manager(dbus.service.Object):

    @dbus.service.method("argo.Manager", in_signature='sss', out_signature='')
    def RegisterService(self, serviceType, instanceId, address):
        print "Service registered: "+serviceType+"{id:"+instanceId+" @"+address+"}\n"

    @dbus.service.method("argo.Manager", in_signature='ss', out_signature='ssb')
    def GetService(self, serviceType, clientId):
	return ("/dev/ttyS0", "HeadMountedGPS", True)

    @dbus.service.method("argo.Manager", in_signature='s', out_signature='')
    def Exit(self, password):
	if (password=="qwerty"): mainloop.quit()


if __name__ == '__main__':
    dbus.mainloop.glib.DBusGMainLoop(set_as_default=True)

    session_bus = dbus.SessionBus()
    name = dbus.service.BusName("argo.Manager", session_bus)
    object = Manager(session_bus, '/Manager')

    mainloop = gobject.MainLoop()
    print "Running manager service."
    mainloop.run()
