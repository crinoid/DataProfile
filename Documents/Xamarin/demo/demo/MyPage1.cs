using System;

using Xamarin.Forms;

namespace demo
{
	public class MyPage1 : ContentPage
	{
		public MyPage1 ()
		{
			Content = new StackLayout { 
				Children = {
					new Label { Text = "Hello ContentPage" }
				}
			};
		}
	}
}


