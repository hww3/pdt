
  string d;
//  string errors="", warnings="";
  array warnings = ({});
  array errors = ({});
  
  array get()
  {
    return errors;
  }

  //!
  array get_warnings()
  {
    return warnings;
  }

  //!
  void print_warnings(string prefix) {
   // if(warnings && strlen(warnings))
   //   werror(prefix+"\n"+warnings);
  }

  //!
  void got_error(string file, int line, string err, int|void is_warning)
  {
    if (file[..sizeof(d)-1] == d) {
      file = file[sizeof(d)..];
    }
    if( is_warning)
      warnings += ({({ file, line ? (string) line : "-", err }) });
    else
      errors += ({({ file, line ? (string) line : "-", err }) });
  }
  
  //!
  void compile_error(string file, int line, string err)
  {
    got_error(file, line, "Error: " + err);
  }
  
  //!
  void compile_warning(string file, int line, string err)
  {
    got_error(file, line, "Warning: " + err, 1);
  }
 
  //!
  void create()
  {
    d = getcwd();
    if (sizeof(d) && (d[-1] != '/') && (d[-1] != '\\'))
      d += "/";
  }
