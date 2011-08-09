from django.shortcuts import render_to_response
from django.template import RequestContext
from django.http import HttpResponseNotFound
from sms.models import Sms
from sms.forms import SmsForm

def index(request):
    if request.method == 'GET':
        for k, v in request.GET.iteritems():
            print "%s: %s" % (k, v)
        smsForm = SmsForm(request.GET)
        if not smsForm.is_valid():
            print "=== ERROR FIELDS ==="
            for e in smsForm.errors:
                print "Field: %s" % (e,)
            return HttpResponseNotFound('<h1>Invalid SMS</h1>') 
        smsForm.save()
        return render_to_response('sms/OK.html')
    else:
        return HttpResponseNotFound('<h1>POST method is not supported!</h1>')
