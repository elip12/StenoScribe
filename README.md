# StenoScribe
### Android App for CSE118
#### Eli Pandolfo, Hannah Wong, Matthew Stone

## Description
StenoScribe aims to simplify and streamline note taking during meetings.
Our app makes it easy to store all photos, documents, recordings, and
transcriptions for a meeting in a single place, and organize your stored
meetings so it’s easy to find the information you need later.

## Todo
*For interim presentation 11/12/19:*
Eli:
    - meeting uids are uuids. Order meetings by date.
    - sync remote to local only syncs meetings you have access to
    - record until manually turn off
    - store all info in db, none on disk
        - documents doesnt change
        - photos need to be converted to bit strings, compressed, and stored in db
        - recordings can store compressed or raw transcription

Hannah:
-   taking image from gallery and storing it to db should work

Matthew:
-   click on document link opens google doc in browser
    also add document from google docs browser (unlikely to have by time)


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

