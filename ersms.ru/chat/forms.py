from django import forms

class ChatForm(forms.Form):
    message = forms.CharField(label='', widget=forms.Textarea(attrs={'cols': 20, 'rows': 5}), max_length=200)
