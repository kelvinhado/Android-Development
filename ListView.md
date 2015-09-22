##CREATE A LISTVIEW FROM A LIST OF OBJECT

Our objective here is to display a custom ListView in our Activity using a list of object
In this case, we will use a list of Student with a firstname and a lastname.



###1) Add in the activity XML a ListView item
*activity_main.xml*
```xml
<ListView
    android:id="@+id/listViewStudent"
    (...) />
```

Attributes of the Activity:
- studentList : contain our list of Student objects.
- lvStudent : this is the listview that will match the listview you define in the activity xml.
- lvAdapter : THIS class adapter will be created in part 3.

*Activity.java*
```java
    private ListView lvStudent;
    private List<Student> studentList;
    private ListAdapter lvAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);

        	lvStudent = (ListView) findViewById(R.id.listViewStudent);
			    lvAdapter = new ListAdapter(this, studentList);
			    lv.setAdapter(lvAdapter);*
    }

```



###2) create the XML Layout that will be use to display each item of our list

The good things is that we can completly customize this layout, adding images, TextViews...
So now we will add two TextViews that will at the end display the firstname and lastname.

*listview_item_student*
```xml
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout (...) >

    <TextView
        android:id="@+id/element1"
			(...) />

    <TextView
        android:id="@+id/element2"
			(...) />

</RelativeLayout>
```



###3) create the Adapter

This is the most important part, We will create a class that will provide us a constructor which will take
as argument, the context of the activity and the list of object to display.
You just need to copy/paste the code and adapt it to your own project.

*ListAdapter class*
```java
public class ListAdapter extends ArrayAdapter {

    List<Student> listStudent;
    LayoutInflater mInflater;
    Context context;

    public ListAdapter(Context context, List<Student> list) {
        super(context, 0, list);
        this.listStudent = list;
        this.context = context;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null)
        {
            convertView = mInflater.inflate(R.layout.listview_item_student,parent,false);
            holder = new ViewHolder();

            // ** CHANGE HERE ** //
            // we do the mapping with our xml textviews already define in part 2)
            holder.element1 = (TextView) convertView.findViewById(R.id.element1);
            holder.element2 =(TextView) convertView.findViewById(R.id.element2);

            // we set a tag to our view to re-use it
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)convertView.getTag();
        }

        // ** CHANGE HERE ** //
        // we finally set our values here
        Student student = listStudent.get(position);
        holder.element1.setText(student.getFirstName());
        holder.element2.setText(student.getLastName())
        return convertView;

    }

    static class ViewHolder
    {
        // ** CHANGE HERE ** //
        TextView element1;
        TextView element2;
    }
}

```



### To SumUp..

This pattern (testing convertView and setting a tag) is very good for the memory,
In fact we are only displaying 8 items (for example) on screen
because it's not necessary to load all the view if we don't see all of them.
having 800 objects will be the same as having 10 objects in the list (in term of memory consumption).

We test if the convertView is null ?

NULL : it's usually null when we start the activity.

NOT NULL : means that the view is already use so we will re-use it to display data.
