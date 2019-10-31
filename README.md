# StenoScribe
### Android App for CSE118
#### Eli Pandolfo, Hannah Wong, Matthew Stone

## Description
StenoScribe aims to simplify and streamline note taking during meetings.
Our app makes it easy to store all photos, documents, recordings, and
transcriptions for a meeting in a single place, and organize your stored
meetings so it’s easy to find the information you need later.

## Todo
*For Tuesday 11/5/19:*
- record activity
- documents activity
- photos activity

*For interim presentation 11/12/19:*
- share activity
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


## Quick Git tutorial
#### Methodology
1. checkout a new branch from master: `git checkout master; git checkout -b my-new-branch`
2. every time you start working, merge master into your branch: `git pull origin master`
3. while working, commit changes often: `git add firstchangedfile, secondchangedfile ... lastchangedfile; git commit -m "short description of changes"`
4. push changes when done working: `git push origin my-new-branch`
5. when you have something stable that can be merged with master: `git checkout master; git merge my-new-branch; git push origin master`

Coordinate with everyone to make sure you are not working on the same parts of the same file at the same time,
to avoid merge conflicts.

If you are unsure that your code works properly, feel free to make a pull request on github
instead of simply merging with master. That way, we can all review it.

