mlog
====

Multi-SSH log viewer

Enables to view logs on several servers multainously via ssh in realtime.
Supports:
* ssh certificate auth
* serverside filtering


It basically streams the result of 'tail | grep' from every server and displays the merged streams.

You can filter logs again locally.


todo
===

* highlight as alternative to filter
* better layout
* better log analysis (parsing of log4j formats)
* more local filters (loglevel, server)
