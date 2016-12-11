using System;

using Xamarin.Forms;

namespace demo
{
	public class Stock : ContentPage
	{
		public Stock ()
		{
			Content = new StackLayout { 
				Children = {
					new Label { Text = "Hello ContentPage" }
				}
			};
		}
	}
}


