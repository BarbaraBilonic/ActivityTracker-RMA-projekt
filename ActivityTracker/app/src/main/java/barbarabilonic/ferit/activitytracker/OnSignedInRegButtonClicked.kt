package barbarabilonic.ferit.activitytracker

interface OnSignedInRegButtonClicked {
   fun onSignedInButtonClicked(email:String,password:String)
   fun onForgotPasswordButtonClicked()
   fun onSignedInRegButtonClicked()
   fun onRegisterButtonClicked(email:String,password: String)
   fun onSendEmailButtonClicked(email:String)
}