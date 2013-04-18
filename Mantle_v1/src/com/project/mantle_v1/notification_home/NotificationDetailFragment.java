package com.project.mantle_v1.notification_home;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.xml.sax.SAXException;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.project.mantle_v1.MantleFile;
import com.project.mantle_v1.MyApplication;
import com.project.mantle_v1.MyHandler;
import com.project.mantle_v1.R;
import com.project.mantle_v1.User;
import com.project.mantle_v1.database.MioDatabaseHelper;
import com.project.mantle_v1.gmail.Sender;
import com.project.mantle_v1.parser.MantleMessage;
import com.project.mantle_v1.parser.ParseJSON;
import com.project.mantle_v1.xml.WriterXml;

/**
 * A fragment representing a single Notification detail screen. This fragment is
 * either contained in a {@link NotificationListActivity} in two-pane mode (on
 * tablets) or a {@link NotificationDetailActivity} on handsets.
 */
public class NotificationDetailFragment extends Fragment {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";

	/**
	 * The dummy content this fragment is presenting.
	 */
	private Notifica mItem;

	private String TAG = NotificationDetailFragment.class.getName();

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */

	public NotificationDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_ITEM_ID)) {
			// Load the dummy content specified by the fragment
			// arguments. In a real-world scenario, use a Loader
			// to load content from a content provider.
			mItem = MyHandler.ITEM_MAP.get(getArguments()
					.getString(ARG_ITEM_ID));
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = null;

		if (mItem != null) {
			/* ==========  AMICIZIA ACCETTATA O RIFIUTATA ================ */
			if (mItem.getNotificationType().equals(
					MantleMessage.FRIENDSHIP_ACCEPTED)
					|| mItem.getNotificationType().equals(
							MantleMessage.FRIENDSHIP_DENIED)
					|| mItem.getNotificationType().equals(MantleMessage.SYSTEM)) {

				rootView = inflater.inflate(R.layout.no_button_fragment,
						container, false);

				((TextView) rootView.findViewById(R.id.SystemInfo))
						.setText(mItem.getNotificationBody());

			}
			
			/* ==========  RICHIESTA D'AMICIZIA ================ */
			else if (mItem.getNotificationType().equals(
					MantleMessage.FRIENDSHIP_REQUEST)) {

				rootView = inflater.inflate(R.layout.two_button_fragment,
						container, false);

				((TextView) rootView.findViewById(R.id.FriendshipRequest))
						.setText(mItem.getNotificationBody());

				final Button bDenied = (Button) rootView
						.findViewById(R.id.RifiutaFriend);
				final Button bAccept = (Button) rootView
						.findViewById(R.id.accetta);
				bAccept.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// new ShowToast().showToast("Accetta",v.getContext());
						MioDatabaseHelper db = new MioDatabaseHelper(v
								.getContext());
						db.insertUser(mItem.getUser().getEmail(), mItem
								.getUser().getUsername(), mItem.getUser()
								.getName(), mItem.getUser().getSurname(), mItem
								.getUser().getKey());

						ParseJSON parser = new ParseJSON(new StringWriter());
						try {
							parser.writeJson(new User(v.getContext()));
						} catch (IOException e) {
							Log.e(TAG, e.getMessage());
						}
						// db.close();

						new Sender(v.getContext(), parser.toString(), mItem
								.getUser().getEmail(),
								MantleMessage.FRIENDSHIP_ACCEPTED).execute();
						Toast.makeText(
								v.getContext(),
								mItem.getUser().getLongName()
										+ " è stato aggiunto alla tua lista amici",
								Toast.LENGTH_LONG).show();
						db.close();
						bAccept.setEnabled(false);
						bDenied.setEnabled(false);
					}
				});

				bDenied.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						ParseJSON parser = new ParseJSON(new StringWriter());
						try {
							User user = new User(v.getContext());
							Note note = new Note(
									user.getUsername(),
									user.getLongName()
											+ " ha rifiutato la tua richiesta d'amicizia");
							parser.writeJson(note);
						} catch (IOException e) {
							Log.e(TAG, e.getMessage());
						}

						new Sender(v.getContext(), parser.toString(), mItem
								.getUser().getEmail(),
								MantleMessage.FRIENDSHIP_DENIED).execute();
						bAccept.setEnabled(false);
						bDenied.setEnabled(false);
					}
				});

			}
			/* ========== COMMENTO ALLA FOTO ================ */
			else if(mItem.getNotificationType().equals(
					MantleMessage.NOTE)) {
				rootView = inflater.inflate(R.layout.fragment_photo_sharing,
						container, false);

				TextView tw = (TextView) rootView.findViewById(R.id.linkText);
				tw.setText(mItem.getTitle());

				Button bComment = (Button) rootView.findViewById(R.id.comment);
				bComment.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent myIntent = new Intent(getActivity(),
								NoteActivity.class);
						Bundle bundle = new Bundle();
						bundle.putString("username", ((MyApplication)getActivity().getApplication()).getUsername());
						bundle.putString("url", mItem.getNote().getCommentLink());
						bundle.putString("email",mItem.getNote().getSender_mail());
						myIntent.putExtra("bundle", bundle);
						getActivity().startActivity(myIntent);
					}
				});
				MioDatabaseHelper db = new MioDatabaseHelper(rootView.getContext());
				String fileUrl = db.getLinkfromLinkComment(mItem.getNote().getCommentLink());

				File comment = MantleFile.downloadFileFromUrl(mItem.getNote().getCommentLink(), "CommentTmp");
				WriterXml xml = new WriterXml();
				try {
					xml.addComment(mItem.getNote().getUser(), mItem.getData(), mItem.getNote().getContent(), comment);
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (TransformerFactoryConfigurationError e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (TransformerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				File img = MantleFile.downloadFileFromUrl(fileUrl, "provaImg");
				ImageView iv = (ImageView) rootView
						.findViewById(R.id.sharedImage);
				iv.setImageBitmap(BitmapFactory.decodeFile(img.getAbsolutePath()));
			}
			/* ============== CONDIVISIONE DI UNA FOTO =======================*/
			else {
				rootView = inflater.inflate(R.layout.fragment_photo_sharing,
						container, false);

				TextView tw = (TextView) rootView.findViewById(R.id.linkText);
				tw.setText(mItem.getTitle());

				Button bComment = (Button) rootView.findViewById(R.id.comment);
				bComment.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent myIntent = new Intent(getActivity(),
								NoteActivity.class);
						Bundle bundle = new Bundle();
						bundle.putString("username", ((MyApplication)getActivity().getApplication()).getUsername());
						bundle.putString("url", mItem.getmFile()
								.getLinkComment());
						bundle.putString("email",mItem.getmFile().getSender_email());
						myIntent.putExtra("bundle", bundle);
						getActivity().startActivity(myIntent);
					}
				});

				MantleFile mFile = mItem.getmFile();
				mFile.downloadFileFromUrl("prova");
				ImageView iv = (ImageView) rootView
						.findViewById(R.id.sharedImage);
				iv.setImageBitmap(mFile.getBitmap());
				MioDatabaseHelper db = new MioDatabaseHelper(rootView.getContext());
				
				/* ===== TODO: Sostituire la stringa vuota con la chiave di cifratura ============ */
				
				long ID = db.insertFile(mFile.getFileName(), mFile.getLinkFile(), mFile.getLinkComment(), "");
				int ID_User = db.getId(mFile.getSender_email());
				db.insertShare((int)ID, ID_User);
			}
		}
		return rootView;
	}
}