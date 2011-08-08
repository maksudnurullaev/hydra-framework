from django.conf.urls.defaults import *
from django.views.generic import DetailView, ListView

urlpatterns = patterns('',
    (r'^sms/', 'chat.views.sms'),
    (r'^$', 'chat.views.index'),
)
