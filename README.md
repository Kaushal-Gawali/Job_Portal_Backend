# Job_Portal_Backend

## Known Limitations
- Resume file uploads are stored on local disk. On the deployed version (Railway), 
  this storage is ephemeral — uploaded files will be lost on redeploy/restart. 
  A production version would use S3/Cloudinary for persistent object storage.
