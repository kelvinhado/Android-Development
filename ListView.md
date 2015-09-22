#CREATE A LISTVIEW FROM A LIST OF OBJECT

1) create in the activity a ListView item in the XML

you'll have first to create and populate the list object (with getter)

```
    private ListView lvStudent;
    private List<Student> studentList;
    private ListAdapter lvAdapter;   //custom list adatpeur


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);

		  // listViewStudent is the id of our ListView in the activity XML File
        	lvStudent = (ListView) findViewById(R.id.listViewStudent);
			lvAdapter = new ListAdapter(this, studentList);
			lv.setAdapter(lvAdapter);
    }

```

2) create the XML Layout for each item of our listView in the Layout Folder


```
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

3) create ListAdapter 


```
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

        /* we first test if the convertview is null (for the first raws when we load the activity)

         */
        if (convertView == null) {

            convertView = mInflater.inflate(R.layout.listview_item_student,parent,false);
            // inflate custom layout called row
            holder = new ViewHolder();
            // we do the mapping with our xml textviews
            holder.element1 = (TextView) convertView.findViewById(R.id.element1);
            holder.element2 =(TextView) convertView.findViewById(R.id.element2);

            // we set a tag to our view to re-use it
            convertView.setTag(holder);
        }
        /*
            else / we already create a view and we will use this space
            we re-uase the older view to display data, so this will pattern we can display 900 rows with the same
            consumption.
         */
        else
        {
            holder = (ViewHolder)convertView.getTag();
        }


        // we finally set our values here
        Student student = listStudent.get(position);
        holder.element1.setText(student.getFirstname());
        holder.element2.setText("" + position)

        return convertView;

    }

    static class ViewHolder
    {
        // ici les elements xml 
        TextView element1;
        TextView element2;

    }
}

```
