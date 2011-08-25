package net.frubar.frupic;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.TextView;

public class About extends Activity {
	private static final String TAG = "FruPic";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);

    	// Log.d(TAG, "About()");
		String HihnOrgUrl = "https://hihn.org";
		String GithubUrl = "https://github.com/rain0r/FruPic-for-Android";
		
		// display author and github url
		try {
			TextView tvAboutAuthorUrl = (TextView) findViewById(R.id.about_content_author_url);
			TextView tvAboutGithubUrl = (TextView) findViewById(R.id.about_content_github_url);
		
			tvAboutAuthorUrl.setText( Html.fromHtml( "<a href=\""+HihnOrgUrl+"\">"+HihnOrgUrl+"</a>" ) );
			tvAboutAuthorUrl.setMovementMethod(LinkMovementMethod.getInstance());

			tvAboutGithubUrl.setText( Html.fromHtml( "<a href=\""+GithubUrl+"\">"+GithubUrl+"</a>" ) );
			tvAboutGithubUrl.setMovementMethod(LinkMovementMethod.getInstance());
		}
		catch (Exception e) {
			// Log.e(TAG, "Exception" , e);
		}
	}
}
