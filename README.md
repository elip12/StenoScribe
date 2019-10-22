# StenoScribe
### Android App for CSE118
#### Eli Pandolfo, Hannah Wong, Matthew Stone

## Description
StenoScribe aims to simplify and streamline note taking during meetings.
Our app makes it easy to store all photos, documents, recordings, and
transcriptions for a meeting in a single place, and organize your stored
meetings so it’s easy to find the information you need later.

## Todo
- controller for main activity
    - Hannah
- layout for bottom navigation activity
    - Eli
- controller for bottom navigation activity
    - Eli
- model for sqlite db
    - see photo for schema (schema.jpg)
    - rooms api for ease of access
    - class for each table
    - dao interface for each table
    - single database class that extends RoomDatabase
    - single database instance built with Room.databaseBuilder(
        getApplicationContext(), MyDatabase.class, "my-database").build()
- record activity
    - layout: Matthew
- share activity
- documents activity
- photos activity
- model for speech-to-text
    - imagebutton for starting recording
    - RecognizerIntent with flags:
        ACTION_RECOGNIZE_SPEECH
        LANGUAGE_MODEL_FREE_FORM
        EXTRA_PROMPT
    ```
    SPEECH_CODE = 1;
    import android.speech.RecognizerIntent;
    import android.content.ActivityNotFoundException;
    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
    intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
    try {
            startActivityForResult(intent, SPEECH_CODE);
        } catch (ActivityNotFoundException a) {
            // print "speech to text not supported"          
        }
    ```
    - override onActivityResult
        - requestCode == SPEECH_CODE: 
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);



