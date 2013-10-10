mlog
====

Multi-SSH log viewer

Enables to view logs on several servers multainously via ssh in realtime.
Supports:
* ssh certificate auth
* serverside filtering


It basically streams the result of 'tail | grep' from every server and displays the merged streams.

You can filter logs again locally.

More informations are available on http://cubiccow.blogspot.de/2013/10/multitail-server-logs-on-windows.html


todo
===

* highlight as alternative to filter
* better layout
* better log analysis (parsing of log4j formats)
* more local filters (loglevel, server)
