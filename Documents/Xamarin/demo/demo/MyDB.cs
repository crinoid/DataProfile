using System;

using Xamarin.Forms;

namespace demo
{
	public class MyDB : ContentPage
	{
		public MyDB ()
		{
			Content = new StackLayout { 
				Children = {
					new Label { Text = "Hello ContentPage" }
				}
			};
		}
	}
}


