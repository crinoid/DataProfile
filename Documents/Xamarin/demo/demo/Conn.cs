using System;

using Xamarin.Forms;

namespace demo
{
	public class Sql : ContentPage
	{
		public interface ISQLite {
			SQLiteConnection GetConnection();
		}
	}
}


