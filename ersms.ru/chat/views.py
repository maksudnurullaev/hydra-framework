from django.shortcuts import render_to_response
from django.template import RequestContext
from chat.models import Chat
from chat.forms import ChatForm
from datetime import datetime

def index(request):
    latest_chat_list = Chat.objects.all().order_by('-pub_date')[:20]
    message = None
    form = None
    activation_code = "S1011"
    if request.method == 'POST':
        form = ChatForm(request.POST)
        if form.is_valid():
            message = form.cleaned_data['message']
            c = Chat(message=message, activation_code=activation_code, pub_date=datetime.now())
            c.save()
    else:
        form = ChatForm()
    return render_to_response('chat/index.html'
                , { 'latest_chat_list': latest_chat_list, 
                    'message': message, 
                    'activation_code':activation_code , 
                    'form': form }
                , context_instance=RequestContext(request))

