from django import forms
from models import Sms

class SmsForm(forms.ModelForm):
    class Meta:
        model = Sms
