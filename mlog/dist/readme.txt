Mlog V 1.0

A SSH-logviewer that tails logs on multiple servers in realtime.




=== Configuration ===

For configuration, please customize both properties-files in the root-directory.
Set your credentials in application.properties. add a private key, if appropriate.

For specifying your sources to be tailed, add them to sources.properties.
Example:
live_logs.1=server:/path/to/logfile.log.{0,date,yyyy-MM-dd}
live_logs.2=server:/path/to/logfile.log.{0,date,yyyy-MM-dd}

this specifies two sources for the configuration "live_logs" with the date in the specified format appendet.
