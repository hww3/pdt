#!/usr/local/bin/pike

constant default_port = 8908; 
constant my_version = "0.0";

object rf;

Protocols.HTTP.Server.Port port;

int main(int argc, array(string) argv) 
{ 
	int my_port = default_port; 
	if(argc>1) my_port=(int)argv[1];

    write("PDT Validator starting on port " + my_port + "\n");

   port = Protocols.HTTP.Server.Port(handle_request, my_port); 
   return -1; 
}

void handle_request(Protocols.HTTP.Server.Request request) 
{ 
	write(sprintf("Got request: %O\n", request));

	mapping response = ([]);

	if(catch(response = parse(request)))
	{
		response = (["error": 500, "type": "text/html", "data": "an unknown error has occurred."]);
	}

	request->response_and_finish(response); 
}

mixed parse(object request)
{

  if(!rf) rf=this;


    mapping m;
    int off = search(request->raw, "\r\n\r\n");

    if(off<=0) error("invalid request format.\n");

    object X;

    if(catch(X=Protocols.XMLRPC.decode_call(request->raw[(off+4) ..])))
    {
      error("Error decoding the XMLRPC Call. Are you not speaking XMLRPC?\n");
    }


    if(rf && rf[X->method_name] &&
        functionp(rf[X->method_name]))
    {

      mixed response;
      mixed err=catch(
        response=call_function(rf[X->method_name], @(X->params + ({request}) )));
      if(err)
      {
        m=([ "data": Protocols.XMLRPC.encode_response_fault(1, err[0]),
          "type": "text/xml" ]);
      }
      else
        m=([ "data": Protocols.XMLRPC.encode_response( ({response}) ),
          "type": "text/xml" ]);
    }
    else
    {
      m=([ "data": Protocols.XMLRPC.encode_response_fault(1, "Method " +
        X->method_name + " not present.\n"),
        "type": "text/xml" ]);
    }
    return (m);

}

mixed validate(string code, string fn)
{
	.error_handler e = .error_handler();
	master()->set_inhibit_compile_errors(e);
	mixed errors = catch {
	program p = compile_string(code, fn);
	};
	
	master()->set_inhibit_compile_errors(0);
	if(errors)
	{
		// werror("%O\n", e->get());
	  return e->get();
	}
}

