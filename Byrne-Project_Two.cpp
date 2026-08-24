//============================================================================
// Name        : ProjectTwo.cpp
// Author      : Patrick Byrne
// Course      : CS 300: Data Structures and Algorithms
// Description : ABCU advising assistance program. Loads the course catalog
//               from a comma separated file into a binary search tree, prints
//               the catalog in alphanumeric order, and looks up a single
//               course with its prerequisites.
//
// Structure   : Binary search tree, per the Project One recommendation. The
//               in-order traversal returns the catalog already sorted, so the
//               print path is O(n) with no sort step anywhere in the program.
//               Search is O(log n) on a reasonably balanced tree.
//
// Note on insertion order: a plain BST does not balance itself, so nearly
// sorted input inserted in file order produces a skewed tree that degrades to
// O(n). The provided CSV happens to be shuffled, but the same catalog listed
// in course number order builds a tree eight levels deep, so the program does
// not rely on the file arriving in a convenient order. The loader reads every
// line first, sorts, then inserts the middle element of each range as the root
// of that subtree, which holds the tree at minimum height for any input order.
//============================================================================

#include <algorithm>
#include <cctype>
#include <fstream>
#include <iostream>
#include <sstream>
#include <string>
#include <vector>

using namespace std;

// File names tried when the user presses Enter at the load prompt. SNHU ships
// this file with spaces in the name, but a browser download often saves it with
// underscores, so both spellings get a try before giving up.
const string DEFAULT_FILE_NAMES[] = {
    "CS 300 ABCU_Advising_Program_Input.csv",
    "CS_300_ABCU_Advising_Program_Input.csv"
};
const int DEFAULT_FILE_COUNT = 2;

//============================================================================
// Course data
//============================================================================

// Holds one line of the course file. Prerequisites are a vector because a
// course can have zero, one, or several.
struct Course {
    string courseNumber;
    string title;
    vector<string> prerequisites;
};

// One node of the binary search tree
struct Node {
    Course course;
    Node* left;
    Node* right;

    Node(const Course& aCourse) : course(aCourse), left(nullptr), right(nullptr) {}
};

//============================================================================
// Binary search tree
//============================================================================

class CourseBST {
public:
    CourseBST();
    ~CourseBST();

    void Insert(const Course& course);
    bool Search(const string& courseNumber, Course& foundCourse) const;
    void PrintInOrder() const;
    void Clear();
    bool IsEmpty() const;

private:
    Node* root;

    void addNode(Node* node, const Course& course);
    void printInOrder(Node* node) const;
    void destroyTree(Node* node);
};

// Start with an empty tree
CourseBST::CourseBST() {
    root = nullptr;
}

// Release every node so the program does not leak on exit
CourseBST::~CourseBST() {
    destroyTree(root);
}

// Post-order delete. Children go before the parent or the pointers are lost.
void CourseBST::destroyTree(Node* node) {
    if (node == nullptr) {
        return;
    }
    destroyTree(node->left);
    destroyTree(node->right);
    delete node;
}

// Empty the tree without destroying the object, so the user can load again
void CourseBST::Clear() {
    destroyTree(root);
    root = nullptr;
}

bool CourseBST::IsEmpty() const {
    return root == nullptr;
}

// Public insert. Handles the empty tree case, then hands off to addNode.
void CourseBST::Insert(const Course& course) {
    if (root == nullptr) {
        root = new Node(course);
    }
    else {
        addNode(root, course);
    }
}

// Walk down from node and hang the new course off the correct empty branch.
// Smaller course numbers go left, larger go right, duplicates are ignored.
void CourseBST::addNode(Node* node, const Course& course) {
    if (course.courseNumber < node->course.courseNumber) {
        if (node->left == nullptr) {
            node->left = new Node(course);
        }
        else {
            addNode(node->left, course);
        }
    }
    else if (course.courseNumber > node->course.courseNumber) {
        if (node->right == nullptr) {
            node->right = new Node(course);
        }
        else {
            addNode(node->right, course);
        }
    }
    // equal course number means it is already in the tree, so nothing to do
}

// Iterative search. Returns true and fills foundCourse when the course number
// matches, false when the walk runs off the bottom of the tree.
bool CourseBST::Search(const string& courseNumber, Course& foundCourse) const {
    Node* current = root;

    while (current != nullptr) {
        if (courseNumber == current->course.courseNumber) {
            foundCourse = current->course;
            return true;
        }
        else if (courseNumber < current->course.courseNumber) {
            current = current->left;
        }
        else {
            current = current->right;
        }
    }

    return false;
}

// Public print. This is the whole reason the tree was chosen: the traversal
// hands back alphanumeric order with no sort call.
void CourseBST::PrintInOrder() const {
    printInOrder(root);
}

// Left subtree, then this node, then right subtree
void CourseBST::printInOrder(Node* node) const {
    if (node == nullptr) {
        return;
    }
    printInOrder(node->left);
    cout << node->course.courseNumber << ", " << node->course.title << endl;
    printInOrder(node->right);
}

//============================================================================
// String helpers
//============================================================================

// Strip leading and trailing whitespace, including the carriage return that
// Windows line endings leave on the last token of every line
string trim(const string& text) {
    size_t first = text.find_first_not_of(" \t\r\n");
    if (first == string::npos) {
        return "";
    }
    size_t last = text.find_last_not_of(" \t\r\n");
    return text.substr(first, (last - first) + 1);
}

// Uppercase a copy so the user can type csci400 and still get a match
string toUpper(const string& text) {
    string result = text;
    for (size_t i = 0; i < result.length(); ++i) {
        result[i] = static_cast<char>(toupper(static_cast<unsigned char>(result[i])));
    }
    return result;
}

// Split one line on commas. Every field is kept, including empty ones, so a
// line with a missing title is caught instead of silently shifting the next
// field into its place. Empty prerequisite fields get dropped later, which is
// what the trailing commas in the input file produce.
vector<string> splitLine(const string& line) {
    vector<string> tokens;
    stringstream stream(line);
    string token;

    while (getline(stream, token, ',')) {
        tokens.push_back(trim(token));
    }

    return tokens;
}

//============================================================================
// File loading
//============================================================================

// Insert the middle course of the range as the subtree root, then do the same
// on each half. Keeps the tree at minimum height no matter how the file was
// ordered. Called only after the vector has been sorted.
void insertBalanced(const vector<Course>& courses, int low, int high, CourseBST& bst) {
    if (low > high) {
        return;
    }

    int middle = low + ((high - low) / 2);
    bst.Insert(courses[middle]);

    insertBalanced(courses, low, middle - 1, bst);
    insertBalanced(courses, middle + 1, high, bst);
}

// Comparison used to sort the courses before the balanced insert
bool compareByCourseNumber(const Course& left, const Course& right) {
    return left.courseNumber < right.courseNumber;
}

// Read the file into the tree. Two passes: the first parses and stores every
// line, the second checks that each prerequisite exists as its own course.
// The check has to wait for the full read because a prerequisite can point at
// a course listed further down the file. Errors set a flag rather than
// returning early, so one run reports everything wrong with the file instead
// of stopping at the first bad line. A file that fails validation is not
// loaded, since advising off a catalog known to be incomplete is worse than
// making the user fix the file first.
bool loadCourses(const string& fileName, CourseBST& bst) {
    ifstream inputFile(fileName.c_str());

    if (!inputFile.is_open()) {
        cout << "Error: could not open file " << fileName << "." << endl;
        return false;
    }

    vector<Course> courses;
    string line;
    int lineNumber = 0;
    bool fileIsValid = true;

    // PASS ONE: parse every line into a Course
    while (getline(inputFile, line)) {
        ++lineNumber;

        if (trim(line).empty()) {
            continue;
        }

        vector<string> tokens = splitLine(line);

        // Every valid line needs a course number and a title in the first two
        // fields. Checking them by position catches a line like CSCI200,,CSCI101
        // where the title is blank and the prerequisite would slide into it.
        if (tokens.size() < 2 || tokens[0].empty() || tokens[1].empty()) {
            cout << "Error: line " << lineNumber
                 << " is missing a course number or title." << endl;
            fileIsValid = false;
            continue;
        }

        Course course;
        course.courseNumber = toUpper(tokens[0]);
        course.title = tokens[1];

        // Anything after the title is a prerequisite. Empty fields here are
        // just the trailing commas the input file uses to pad short lines.
        for (size_t i = 2; i < tokens.size(); ++i) {
            if (!tokens[i].empty()) {
                course.prerequisites.push_back(toUpper(tokens[i]));
            }
        }

        courses.push_back(course);
    }

    inputFile.close();

    if (courses.empty()) {
        cout << "Error: no valid course data found in " << fileName << "." << endl;
        return false;
    }

    // Sorting here serves two purposes: it feeds the balanced insert below,
    // and it lets the prerequisite check use binary_search instead of a
    // nested scan.
    sort(courses.begin(), courses.end(), compareByCourseNumber);

    vector<string> courseNumbers;
    for (size_t i = 0; i < courses.size(); ++i) {
        courseNumbers.push_back(courses[i].courseNumber);
    }

    // PASS TWO: every prerequisite must exist as a course of its own
    for (size_t i = 0; i < courses.size(); ++i) {
        for (size_t j = 0; j < courses[i].prerequisites.size(); ++j) {
            string prerequisite = courses[i].prerequisites[j];

            if (!binary_search(courseNumbers.begin(), courseNumbers.end(), prerequisite)) {
                cout << "Error: " << courses[i].courseNumber
                     << " lists prerequisite " << prerequisite
                     << ", which is not a course in this file." << endl;
                fileIsValid = false;
            }
        }
    }

    // Nothing gets loaded from a file that failed validation. The tree keeps
    // whatever it held before, so a bad load does not throw away a good one.
    if (!fileIsValid) {
        cout << "Load failed. Correct the errors above and load the file again." << endl;
        return false;
    }

    // Wipe anything from a previous load so reloading does not stack catalogs
    bst.Clear();
    insertBalanced(courses, 0, static_cast<int>(courses.size()) - 1, bst);

    cout << courses.size() << " courses loaded." << endl;
    return true;
}

//============================================================================
// Display
//============================================================================

// Print one course, then each prerequisite with its own title. The directions
// ask for prerequisite course numbers and titles, so each prerequisite gets
// looked up in the tree to pull its title. The label stays on the first line
// and the rest align under it.
void displayCourse(const Course& course, const CourseBST& courseTree) {
    cout << course.courseNumber << ", " << course.title << endl;

    if (course.prerequisites.empty()) {
        cout << "Prerequisites: None" << endl;
        return;
    }

    const string label = "Prerequisites: ";
    const string indent(label.length(), ' ');

    for (size_t i = 0; i < course.prerequisites.size(); ++i) {
        cout << (i == 0 ? label : indent) << course.prerequisites[i];

        // A successful load rejects any file with a prerequisite that is not a
        // course, so this lookup should always hit. Guarded anyway rather than
        // printing a stray comma and an empty title if it ever does not.
        Course prerequisiteCourse;
        if (courseTree.Search(course.prerequisites[i], prerequisiteCourse)) {
            cout << ", " << prerequisiteCourse.title;
        }

        cout << endl;
    }
}

void displayMenu() {
    cout << endl;
    cout << "  1. Load Data Structure." << endl;
    cout << "  2. Print Course List." << endl;
    cout << "  3. Print Course." << endl;
    cout << "  9. Exit" << endl;
    cout << endl;
    cout << "What would you like to do? ";
}

//============================================================================
// Main
//============================================================================

int main() {
    CourseBST courseTree;
    string choice;
    bool dataLoaded = false;

    cout << "Welcome to the course planner." << endl;

    while (true) {
        displayMenu();

        // Read the menu choice as a string so letters and stray input get the
        // same error message as an out of range number instead of jamming cin
        if (!getline(cin, choice)) {
            break;
        }
        choice = trim(choice);

        if (choice == "1") {
            string fileName;
            cout << "Enter the file name (press Enter for "
                 << DEFAULT_FILE_NAMES[0] << "): ";
            getline(cin, fileName);
            fileName = trim(fileName);

            if (!fileName.empty()) {
                // The user named a file, so use exactly that one
                if (loadCourses(fileName, courseTree)) {
                    dataLoaded = true;
                }
            }
            else {
                // Nothing typed, so try the known names for the shipped file
                bool attempted = false;
                for (int i = 0; i < DEFAULT_FILE_COUNT && !attempted; ++i) {
                    ifstream test(DEFAULT_FILE_NAMES[i].c_str());
                    if (test.is_open()) {
                        test.close();
                        attempted = true;
                        if (loadCourses(DEFAULT_FILE_NAMES[i], courseTree)) {
                            dataLoaded = true;
                        }
                    }
                }

                if (!attempted) {
                    cout << "Error: could not open " << DEFAULT_FILE_NAMES[0]
                         << ". Enter the file name instead." << endl;
                }
            }
        }
        else if (choice == "2") {
            if (!dataLoaded) {
                cout << "Please load the data structure first with option 1." << endl;
            }
            else {
                cout << "Here is a sample schedule:" << endl;
                cout << endl;
                courseTree.PrintInOrder();
            }
        }
        else if (choice == "3") {
            if (!dataLoaded) {
                cout << "Please load the data structure first with option 1." << endl;
            }
            else {
                string courseNumber;
                cout << "What course do you want to know about? ";
                getline(cin, courseNumber);
                courseNumber = toUpper(trim(courseNumber));

                Course foundCourse;
                if (courseTree.Search(courseNumber, foundCourse)) {
                    displayCourse(foundCourse, courseTree);
                }
                else {
                    cout << "Course " << courseNumber << " was not found." << endl;
                }
            }
        }
        else if (choice == "9") {
            cout << "Thank you for using the course planner!" << endl;
            break;
        }
        else if (choice.empty()) {
            cout << "Please enter an option." << endl;
        }
        else {
            cout << choice << " is not a valid option." << endl;
        }
    }

    return 0;
}
