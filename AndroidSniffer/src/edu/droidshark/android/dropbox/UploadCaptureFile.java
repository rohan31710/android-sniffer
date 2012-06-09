/*
 * Copyright (c) 2011 Dropbox, Inc.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package edu.droidshark.android.dropbox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.UploadRequest;
import com.dropbox.client2.ProgressListener;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxFileSizeException;
import com.dropbox.client2.exception.DropboxIOException;
import com.dropbox.client2.exception.DropboxParseException;
import com.dropbox.client2.exception.DropboxPartialFileException;
import com.dropbox.client2.exception.DropboxServerException;
import com.dropbox.client2.exception.DropboxUnlinkedException;

/**
 * ASyncTask for uploading a capture file to dropbox
 */
public class UploadCaptureFile extends AsyncTask<Void, Long, Boolean>
{

	private DropboxAPI<?> api;
	private File file;
	private UploadRequest uploadRequest;
	private Context mContext;
	private final ProgressDialog progressDialog;
	private String errorMsg;

	public UploadCaptureFile(Context context, DropboxAPI<?> api, File file)
	{
		// We set the context this way so we don't accidentally leak activities
		mContext = context.getApplicationContext();

		this.api = api;
		this.file = file;

		progressDialog = new ProgressDialog(context);
		progressDialog.setMax(100);
		progressDialog.setMessage("Uploading " + file.getName());
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setProgress(0);
		progressDialog.setButton(ProgressDialog.BUTTON_NEGATIVE, "Cancel", new OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				// This will cancel the putFile operation
				uploadRequest.abort();
			}
		});
		progressDialog.show();
	}

	@Override
	protected Boolean doInBackground(Void... params)
	{
		try
		{
			// By creating a request, we get a handle to the putFile operation,
			// so we can cancel it later if we want to
			FileInputStream fis = new FileInputStream(file);
			uploadRequest = api.putFileOverwriteRequest("/" + file.getName(), fis, file.length(),
					new ProgressListener()
					{
						@Override
						public long progressInterval()
						{
							// Update the progress bar every half-second or so
							return 500;
						}

						@Override
						public void onProgress(long bytes, long total)
						{
							publishProgress(bytes);
						}
					});

			if (uploadRequest != null)
			{
				uploadRequest.upload();
				return true;
			}

		} catch (DropboxUnlinkedException e)
		{
			// This session wasn't authenticated properly or user unlinked
			errorMsg = "This app wasn't authenticated properly.";
		} catch (DropboxFileSizeException e)
		{
			// File size too big to upload via the API
			errorMsg = "This file is too big to upload";
		} catch (DropboxPartialFileException e)
		{
			// We canceled the operation
			errorMsg = "Upload canceled";
		} catch (DropboxServerException e)
		{
			// This gets the Dropbox error, translated into the user's language
			errorMsg = e.body.userError;
			if (errorMsg == null)
			{
				errorMsg = e.body.error;
			}
		} catch (DropboxIOException e)
		{
			// Happens all the time, probably want to retry automatically.
			errorMsg = "Network error.  Try again.";
		} catch (DropboxParseException e)
		{
			// Probably due to Dropbox server restarting, should retry
			errorMsg = "Dropbox error.  Try again.";
		} catch (DropboxException e)
		{
			// Unknown error
			errorMsg = "Unknown error.  Try again.";
		} catch (FileNotFoundException e)
		{
		}
		return false;
	}

	@Override
	protected void onProgressUpdate(Long... progress)
	{
		int percent = (int) (100.0 * (double) progress[0] / file.length() + 0.5);
		progressDialog.setProgress(percent);
	}

	@Override
	protected void onPostExecute(Boolean result)
	{
		progressDialog.dismiss();
		if (result)
			Toast.makeText(mContext, "Capture file successfully uploaded", Toast.LENGTH_LONG).show();
		else
			Toast.makeText(mContext, "mErrorMsg", Toast.LENGTH_LONG).show();
	}
}
